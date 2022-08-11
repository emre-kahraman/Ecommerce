package com.example.cartservice.service;

import com.example.cartservice.dto.CreateOrderRequest;
import com.example.cartservice.entity.Cart;
import com.example.cartservice.entity.CartItem;
import com.example.cartservice.repository.CartRepository;
import com.example.customerservice.dto.CustomerKafka;
import com.example.productservice.dto.AddItemToCartRequest;
import io.lettuce.core.dynamic.annotation.Key;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@EnableKafka
public class CartService {

    private final CartRepository cartRepository;
    private final KafkaTemplate<String, CreateOrderRequest> kafkaTemplate;
    public ResponseEntity<Cart> getCartById(String id) {
        Optional<Cart> cart = cartRepository.findById(id);
        if(cart.isEmpty())
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(cart.get(), HttpStatus.OK);
    }

    public ResponseEntity<Cart> getCartByUserId(String userId) {
        Optional<Cart> cart = cartRepository.getCartByUserId(userId);
        if(cart.isEmpty())
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(cart.get(), HttpStatus.OK);
    }

    @KafkaListener(topics = "customers", groupId = "cart")
    public void customerListener(@Payload CustomerKafka customerKafka, @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String userId){
        if(customerKafka.getStatus().equals("Delete")){
            Optional<Cart> cart = cartRepository.getCartByUserId(userId);
            cartRepository.deleteById(cart.get().getId());
        }
        else {
            Cart cart = Cart.builder().userId(userId).userName(customerKafka.getName())
                    .userLastName(customerKafka.getLastName()).email(customerKafka.getEmail())
                    .address(customerKafka.getAddress()).totalPrice(BigDecimal.valueOf(0)).build();
            cart.getCartItems().add(CartItem.builder().productId("1").productName("test").build());
            cartRepository.save(cart);
        }
    }

    @KafkaListener(topics = "products", groupId = "cart")
    public void productListener(@Payload AddItemToCartRequest addItemToCartRequest, @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String userId){
        Optional<Cart> cart = cartRepository.getCartByUserId(userId);
        CartItem cartItem = CartItem.builder().productId(addItemToCartRequest.getProductId())
                .productName(addItemToCartRequest.getProductName())
                .unitPrice(addItemToCartRequest.getUnitPrice())
                .quantity(addItemToCartRequest.getQuantity()).build();
        cart.get().addCartItem(cartItem);
        cartRepository.save(cart.get());
    }

    public ResponseEntity<Cart> removeCartItem(String userId, CartItem cartItem) {
        Optional<Cart> cart = cartRepository.getCartByUserId(userId);
        cart.get().removeCartItem(cartItem);
        Cart savedCart = cartRepository.save(cart.get());
        return new ResponseEntity<>(savedCart, HttpStatus.OK);
    }

    public ResponseEntity<HttpStatus> createOrder(String userId) {
        Optional<Cart> cart = cartRepository.getCartByUserId(userId);
        if(cart.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        CreateOrderRequest createOrderRequest = new CreateOrderRequest(cart.get().getUserId(), cart.get().getUserName()
        , cart.get().getUserLastName(), cart.get().getEmail(), cart.get().getAddress(), cart.get().getCartItems(), cart.get().getTotalPrice());
        kafkaTemplate.send("orders", createOrderRequest.getUserId(), createOrderRequest);
        cart.get().getCartItems().clear();
        cart.get().setTotalPrice(BigDecimal.valueOf(0));
        cartRepository.save(cart.get());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

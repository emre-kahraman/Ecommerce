package com.example.cartservice;

import com.example.cartservice.dto.CreateOrderRequest;
import com.example.cartservice.entity.Cart;
import com.example.cartservice.entity.CartItem;
import com.example.cartservice.repository.CartRepository;
import com.example.cartservice.service.CartService;
import com.example.customerservice.dto.CustomerKafka;
import com.example.customerservice.dto.CustomerState;
import com.example.productservice.dto.AddItemToCartRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import redis.embedded.RedisServer;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
public class CartIntegrationTests {

    @Autowired
    CartService cartService;

    @Autowired
    CartRepository cartRepository;

    static RedisServer redisServer;

    @Autowired
    KafkaTemplate<String, CreateOrderRequest> kafkaTemplate;

    @BeforeAll
    public static void startRedis(){
        redisServer = RedisServer.builder().port(6370).build();
        redisServer.start();
    }

    @AfterAll
    public static void closeRedis(){
        redisServer.stop();
    }

    @BeforeEach
    public void setup(){
        Cart cart = Cart.builder().id("1").userId("1").userName("test")
                .userLastName("test").email("test2").cartItems(new HashSet<>())
                .address("test").totalPrice(BigDecimal.valueOf(0)).build();
        Cart cart2 = Cart.builder().id("2").userId("2").userName("test2")
                .userLastName("test2").email("test2").cartItems(new HashSet<>())
                .address("test2").totalPrice(BigDecimal.valueOf(0)).build();
        CartItem cartItem = CartItem.builder().productId("1").
                productName("test")
                .unitPrice(BigDecimal.valueOf(10))
                .quantity(1).build();
        cart2.addCartItem(cartItem);
        cartRepository.deleteAll();
        cartRepository.save(cart);
        cartRepository.save(cart2);
    }

    @Test
    public void itShouldReturnCartById(){

        ResponseEntity<Cart> responseEntity = cartService.getCartById("1");

        assertEquals(responseEntity.getBody().getUserId(), "1");
        assertEquals(responseEntity.getBody().getTotalPrice(), BigDecimal.valueOf(0));
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void itShouldReturnCartByUserId(){

        ResponseEntity<Cart> responseEntity = cartService.getCartByUserId("1");

        assertEquals(responseEntity.getBody().getUserId(), "1");
        assertEquals(responseEntity.getBody().getTotalPrice(), BigDecimal.valueOf(0));
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void itShouldCreateCart(){
        CustomerKafka customerKafka = new CustomerKafka("test", "test"
                , "test@gmail.com", "test", CustomerState.CREATE);
        String userId = "1";

        Cart savedCart = cartService.createCart(customerKafka, userId);

        assertEquals(savedCart.getUserId(), userId);
        assertEquals(savedCart.getUserName(), customerKafka.getName());
        assertEquals(savedCart.getCartItems().size(), 0);
        assertEquals(savedCart.getTotalPrice(), BigDecimal.valueOf(0));
    }

    @Test
    public void itShouldDeleteCart(){

        cartService.deleteCart("1");

        assertEquals(cartRepository.findById("1"), Optional.empty());
    }

    @Test
    public void itShouldUpdateCart(){
        CustomerKafka customerKafka = new CustomerKafka("test3", "test3"
                , "test3@gmail.com", "test3", CustomerState.UPDATE);
        String userId = "1";

        Cart savedCart = cartService.updateCart(customerKafka, userId);

        assertEquals(savedCart.getUserId(), userId);
        assertEquals(savedCart.getUserName(), customerKafka.getName());
        assertEquals(savedCart.getCartItems().size(), 0);
        assertEquals(savedCart.getTotalPrice(), BigDecimal.valueOf(0));
    }

    @Test
    public void itShouldAddCartItem(){

        AddItemToCartRequest addItemToCartRequest = new AddItemToCartRequest("1","1","test",BigDecimal.valueOf(1),1);

        Cart savedCart = cartService.addCartItem(addItemToCartRequest);

        assertEquals(savedCart.getCartItems().size(), 1);
        assertEquals(savedCart.getTotalPrice(), BigDecimal.valueOf(1));
    }

    @Test
    public void itShouldRemoveCartItem(){

        CartItem cartItem = CartItem.builder().productId("1").
                productName("test")
                .unitPrice(BigDecimal.valueOf(10))
                .quantity(1).build();

        ResponseEntity<Cart> responseEntity = cartService.removeCartItem("2", cartItem);

        assertEquals(responseEntity.getBody().getCartItems().size(), 0);
        assertEquals(responseEntity.getBody().getTotalPrice(), BigDecimal.valueOf(0));
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void itShouldCreateOrder(){

        ResponseEntity<HttpStatus> responseEntity = cartService.createOrder("2");

        assertEquals(cartRepository.findById("2").get().getCartItems().size(), 0);
        assertEquals(cartRepository.findById("2").get().getTotalPrice(), BigDecimal.valueOf(0));
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
    }
}

package com.example.cartservice;

import com.example.cartservice.dto.CreateOrderRequest;
import com.example.cartservice.entity.Cart;
import com.example.cartservice.entity.CartItem;
import com.example.cartservice.repository.CartRepository;
import com.example.cartservice.service.CartService;
import com.example.customerservice.dto.CustomerKafka;
import com.example.customerservice.dto.CustomerState;
import com.example.productservice.dto.AddItemToCartRequest;
import com.example.productservice.dto.UpdateCartItemRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceTests {

    @InjectMocks
    CartService cartService;

    @Mock
    CartRepository cartRepository;

    @Mock
    KafkaTemplate<String, CreateOrderRequest> kafkaTemplate;

    @Test
    public void itShouldReturnCartById(){
        Cart cart = Cart.builder().userId("1").userName("test")
                .userLastName("test").email("test")
                .address("test").totalPrice(BigDecimal.valueOf(0)).build();

        when(cartRepository.findById(cart.getId())).thenReturn(Optional.of(cart));

        ResponseEntity<Cart> responseEntity = cartService.getCartById(cart.getId());

        verify(cartRepository).findById(cart.getId());
        assertEquals(responseEntity.getBody().getUserId(), cart.getUserId());
        assertEquals(responseEntity.getBody().getTotalPrice(), cart.getTotalPrice());
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void itShouldReturnCartByUserId(){
        Cart cart = Cart.builder().userId("1").userName("test")
                .userLastName("test").email("test")
                .address("test").totalPrice(BigDecimal.valueOf(0)).build();

        when(cartRepository.getCartByUserId(cart.getUserId())).thenReturn(Optional.of(cart));

        ResponseEntity<Cart> responseEntity = cartService.getCartByUserId(cart.getUserId());

        verify(cartRepository).getCartByUserId(cart.getUserId());
        assertEquals(responseEntity.getBody().getUserId(), cart.getUserId());
        assertEquals(responseEntity.getBody().getTotalPrice(), cart.getTotalPrice());
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void itShouldCreateCart(){
        CustomerKafka customerKafka = new CustomerKafka("test", "test"
                , "test@gmail.com", "test", CustomerState.CREATE);
        String userId = "1";
        Cart cart = Cart.builder().userId("1").userName("test")
                .userLastName("test").email("test").cartItems(new HashSet<>())
                .address("test").totalPrice(BigDecimal.valueOf(0)).build();

        when(cartRepository.save(any())).thenReturn(cart);

        Cart savedCart = cartService.createCart(customerKafka, userId);

        verify(cartRepository).save(any());
        assertEquals(savedCart.getUserId(), userId);
        assertEquals(savedCart.getUserName(), customerKafka.getName());
        assertEquals(savedCart.getCartItems().size(), 0);
        assertEquals(savedCart.getTotalPrice(), BigDecimal.valueOf(0));
    }

    @Test
    public void itShouldDeleteCart(){
        Cart cart = Cart.builder().id("1").userId("1").userName("test")
                .userLastName("test").email("test").cartItems(new HashSet<>())
                .address("test").totalPrice(BigDecimal.valueOf(0)).build();

        when(cartRepository.getCartByUserId(cart.getUserId())).thenReturn(Optional.of(cart));
        doNothing().when(cartRepository).deleteById(cart.getId());

        cartService.deleteCart(cart.getUserId());

        verify(cartRepository).deleteById(cart.getUserId());
    }

    @Test
    public void itShouldUpdateCart(){
        CustomerKafka customerKafka = new CustomerKafka("test2", "test2"
                , "test@gmail.com", "test2", CustomerState.UPDATE);
        String userId = "1";
        Cart cart = Cart.builder().userId("1").userName("test")
                .userLastName("test").email("test").cartItems(new HashSet<>())
                .address("test").totalPrice(BigDecimal.valueOf(0)).build();
        Cart updatedCart = Cart.builder().userId("1").userName("test2")
                .userLastName("test2").email("test2").cartItems(new HashSet<>())
                .address("test2").totalPrice(BigDecimal.valueOf(0)).build();

        when(cartRepository.findById(userId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any())).thenReturn(updatedCart);

        Cart savedCart = cartService.updateCart(customerKafka, userId);

        verify(cartRepository).save(any());
        assertEquals(savedCart.getUserId(), userId);
        assertEquals(savedCart.getUserName(), customerKafka.getName());
        assertEquals(savedCart.getUserLastName(), customerKafka.getLastName());
        assertEquals(savedCart.getTotalPrice(), BigDecimal.valueOf(0));
    }

    @Test
    public void itShouldAddCartItem(){
        Cart cart = Cart.builder().id("1").userId("1").userName("test")
                .userLastName("test").email("test").cartItems(new HashSet<>())
                .address("test").totalPrice(BigDecimal.valueOf(0)).build();

        AddItemToCartRequest addItemToCartRequest = new AddItemToCartRequest("1","1","test",BigDecimal.valueOf(1),1);

        when(cartRepository.getCartByUserId(cart.getUserId())).thenReturn(Optional.of(cart));
        when(cartRepository.save(any())).thenReturn(cart);

        Cart savedCart = cartService.addCartItem(addItemToCartRequest);

        verify(cartRepository).save(any());
        assertEquals(savedCart.getCartItems().size(), 1);
        assertEquals(savedCart.getTotalPrice(), BigDecimal.valueOf(1));
    }

    @Test
    public void itShouldUpdateCartItemForAllCarts(){
        Cart cart = Cart.builder().id("1").userId("1").userName("test")
                .userLastName("test").email("test").cartItems(new HashSet<>())
                .address("test").totalPrice(BigDecimal.valueOf(0)).build();
        Cart cart2 = Cart.builder().id("2").userId("2").userName("test2")
                .userLastName("test2").email("test2").cartItems(new HashSet<>())
                .address("test2").totalPrice(BigDecimal.valueOf(0)).build();
        CartItem cartItem = CartItem.builder().productId("1").
                productName("test")
                .unitPrice(BigDecimal.valueOf(10))
                .quantity(1).build();
        CartItem cartItem2 = CartItem.builder().productId("1").
                productName("test")
                .unitPrice(BigDecimal.valueOf(10))
                .quantity(1).build();
        cart.addCartItem(cartItem);
        cart2.addCartItem(cartItem2);

        UpdateCartItemRequest updateCartItemRequest = new UpdateCartItemRequest("1","test2",BigDecimal.valueOf(2));

        when(cartRepository.findAll()).thenReturn(List.of(cart, cart2));
        when(cartRepository.save(cart)).thenReturn(cart);
        when(cartRepository.save(cart2)).thenReturn(cart);

        List<Cart> cartList = cartService.updateCartItem(updateCartItemRequest);

        assertEquals(cartList.size(), 2);
        assertEquals(cartList.get(0).getTotalPrice(), BigDecimal.valueOf(2));
        assertEquals(cartList.get(1).getTotalPrice(), BigDecimal.valueOf(2));
    }

    @Test
    public void itShouldRemoveCartItem(){
        Cart cart = Cart.builder().userId("1").userName("test")
                .userLastName("test").email("test")
                .address("test").cartItems(new HashSet<>()).totalPrice(BigDecimal.valueOf(0)).build();

        CartItem cartItem = CartItem.builder().productId("1").
                productName("test")
                .unitPrice(BigDecimal.valueOf(10))
                .quantity(1).build();
        cart.addCartItem(cartItem);

        when(cartRepository.getCartByUserId(cart.getUserId())).thenReturn(Optional.of(cart));

        when(cartRepository.save(any())).thenReturn(cart);

        ResponseEntity<Cart> responseEntity = cartService.removeCartItem(cart.getUserId(), cartItem);

        assertEquals(responseEntity.getBody().getCartItems().size(), 0);
        assertEquals(responseEntity.getBody().getTotalPrice(), BigDecimal.valueOf(0));
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void itShouldAddCartItemToCart(){
        Cart cart = Cart.builder().userId("1").userName("test")
                .userLastName("test").email("test")
                .address("test").cartItems(new HashSet<>()).totalPrice(BigDecimal.valueOf(0)).build();

        CartItem cartItem = CartItem.builder().productId("1").
                productName("test")
                .unitPrice(BigDecimal.valueOf(10))
                .quantity(1).build();

        cart.addCartItem(cartItem);

        assertEquals(cart.getCartItems().contains(cartItem), true);
        assertEquals(cart.getTotalPrice(), BigDecimal.valueOf(10));
    }

    @Test
    public void itShouldRemoveCartItemToCart(){
        Cart cart = Cart.builder().userId("1").userName("test")
                .userLastName("test").email("test")
                .address("test").cartItems(new HashSet<>()).totalPrice(BigDecimal.valueOf(0)).build();

        CartItem cartItem = CartItem.builder().productId("1").
                productName("test")
                .unitPrice(BigDecimal.valueOf(10))
                .quantity(1).build();

        cart.addCartItem(cartItem);

        cart.removeCartItem(cartItem);

        assertEquals(cart.getCartItems().contains(cartItem), false);
        assertEquals(cart.getTotalPrice(), BigDecimal.valueOf(0));
    }

    @Test
    public void itShouldUpdateCartItem(){
        Cart cart = Cart.builder().id("1").userId("1").userName("test")
                .userLastName("test").email("test").cartItems(new HashSet<>())
                .address("test").totalPrice(BigDecimal.valueOf(0)).build();
        CartItem cartItem = CartItem.builder().productId("1").
                productName("test")
                .unitPrice(BigDecimal.valueOf(10))
                .quantity(1).build();
        cart.addCartItem(cartItem);

        UpdateCartItemRequest updateCartItemRequest = new UpdateCartItemRequest("2","test2",BigDecimal.valueOf(2));

        cart.updateCartItem(cartItem, updateCartItemRequest);

        assertEquals(cart.getCartItems().size(), 1);
        assertEquals(cart.getTotalPrice(), BigDecimal.valueOf(2));
    }

    @Test
    public void itShouldCreateOrder(){
        Cart cart = Cart.builder().userId("1").userName("test")
                .userLastName("test").email("test")
                .address("test").cartItems(new HashSet<>()).totalPrice(BigDecimal.valueOf(0)).build();

        CartItem cartItem = CartItem.builder().productId("1").
                productName("test")
                .unitPrice(BigDecimal.valueOf(10))
                .quantity(1).build();
        cart.addCartItem(cartItem);

        when(cartRepository.getCartByUserId(cart.getUserId())).thenReturn(Optional.of(cart));

        when(kafkaTemplate.send(eq("orders"), any(), any())).thenReturn(null);

        ResponseEntity<HttpStatus> responseEntity = cartService.createOrder(cart.getUserId());

        assertEquals(cart.getCartItems().size(), 0);
        assertEquals(cart.getTotalPrice(), BigDecimal.valueOf(0));
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
    }
}

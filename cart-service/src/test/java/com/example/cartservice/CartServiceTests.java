package com.example.cartservice;

import com.example.cartservice.entity.Cart;
import com.example.cartservice.entity.CartItem;
import com.example.cartservice.repository.CartRepository;
import com.example.cartservice.service.CartService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CartServiceTests {

    @InjectMocks
    CartService cartService;

    @Mock
    CartRepository cartRepository;

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
}

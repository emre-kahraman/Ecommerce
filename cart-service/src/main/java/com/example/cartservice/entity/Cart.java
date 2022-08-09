package com.example.cartservice.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@RedisHash
public class Cart {

    @Id
    private String id;
    @Indexed
    private String userId;
    private String userName;
    private String userLastName;
    private String email;
    private String address;
    @Builder.Default
    private Set<CartItem> cartItems = new HashSet<>();
    private BigDecimal totalPrice;

    public void addCartItem(CartItem cartItem){
        cartItems.add(cartItem);
        totalPrice = totalPrice.add(cartItem.getUnitPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
    }

    public void removeCartItem(CartItem cartItem){
        cartItems.remove(cartItem);
        totalPrice = totalPrice.subtract(cartItem.getUnitPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
    }
}

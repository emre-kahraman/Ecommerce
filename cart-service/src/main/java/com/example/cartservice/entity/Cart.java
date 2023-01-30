package com.example.cartservice.entity;

import com.example.productservice.dto.UpdateCartItemRequest;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
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

    public void updateCartItem(CartItem cartItem, UpdateCartItemRequest updateCartItemRequest){
        totalPrice = totalPrice.subtract(cartItem.getUnitPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        cartItem.setProductId(updateCartItemRequest.getProductId());
        cartItem.setProductName(updateCartItemRequest.getProductName());
        cartItem.setUnitPrice(updateCartItemRequest.getUnitPrice());
        totalPrice = totalPrice.add(cartItem.getUnitPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
    }
}

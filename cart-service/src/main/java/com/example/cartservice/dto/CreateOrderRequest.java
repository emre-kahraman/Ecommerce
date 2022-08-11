package com.example.cartservice.dto;

import com.example.cartservice.entity.CartItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderRequest {

    private String userId;
    private String userName;
    private String userLastName;
    private String email;
    private String address;
    private Set<CartItem> cartItems;
    private BigDecimal totalPrice;
}

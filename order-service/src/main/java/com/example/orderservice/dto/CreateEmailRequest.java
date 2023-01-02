package com.example.orderservice.dto;

import com.example.cartservice.entity.CartItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateEmailRequest {

    private String orderId;
    private String userName;
    private String userLastName;
    private String email;
    private String address;
    private Set<CartItem> cartItems;
    private BigDecimal totalPrice;
    private Date date;
}

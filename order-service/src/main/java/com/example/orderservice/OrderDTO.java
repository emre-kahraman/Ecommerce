package com.example.orderservice;

import com.example.cartservice.entity.CartItem;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

@Getter
@Setter
@Builder
public class OrderDTO {

    private String userId;
    private String userName;
    private String userLastName;
    private String email;
    private String address;
    private Set<CartItem> cartItems;
    private BigDecimal totalPrice;
    private Date date;
}

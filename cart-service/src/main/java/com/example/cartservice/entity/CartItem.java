package com.example.cartservice.entity;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@EqualsAndHashCode
public class CartItem {

    private String productId;
    private String productName;
    private BigDecimal unitPrice;
    private Integer quantity;
}

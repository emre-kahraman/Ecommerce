package com.example.cartservice.entity;

import lombok.*;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class CartItem {

    private String productId;
    private String productName;
    private BigDecimal unitPrice;
    private Integer quantity;
}

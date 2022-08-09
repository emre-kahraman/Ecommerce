package com.example.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AddItemToCartRequest {

    private String userId;
    private String productId;
    private String productName;
    private BigDecimal unitPrice;
    private Integer quantity;
}

package com.example.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UpdateCartItemRequest {

    private String productId;
    private String productName;
    private BigDecimal unitPrice;
}

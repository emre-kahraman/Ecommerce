package com.example.productservice.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class SaveProductRequest {

    private String name;
    private String category;
    private BigDecimal price;
}

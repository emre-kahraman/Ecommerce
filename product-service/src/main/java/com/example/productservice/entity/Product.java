package com.example.productservice.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document("product")
@Getter
@Setter
@Builder
public class Product {

    @Id
    private String id;
    private String name;
    @Indexed
    private String category;
    private BigDecimal price;
}

package com.example.orderservice.entity;


import com.example.cartservice.entity.CartItem;
import jdk.jfr.Timestamp;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

@Document("orders")
@Getter
@Setter
@Builder
public class Order {

    @Id
    private String id;
    @Indexed
    private String userId;
    private String userName;
    private String userLastName;
    private String email;
    private String address;
    private Set<CartItem> cartItems;
    private BigDecimal totalPrice;
    @Timestamp
    private Date date;
}

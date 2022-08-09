package com.example.customerservice.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("customer")
@Getter
@Setter
@Builder
public class Customer {

    @Id
    private String id;
    private String name;
    private String lastName;
    private String email;
    private String password;
    private String address;

}

package com.example.customerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerKafka {

    private String name;
    private String lastName;
    private String email;
    private String address;
    private CustomerState customerState;
}

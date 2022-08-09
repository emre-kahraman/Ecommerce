package com.example.customerservice.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SaveCustomerRequest {

    private String name;
    private String lastName;
    private String email;
    private String address;
    private String password;
}

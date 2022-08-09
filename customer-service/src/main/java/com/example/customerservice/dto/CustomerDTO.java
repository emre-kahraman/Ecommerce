package com.example.customerservice.dto;

import lombok.*;

@Getter
@Setter
@Builder
public class CustomerDTO {

    private String name;
    private String lastName;
    private String email;
    private String address;
}

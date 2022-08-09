package com.example.customerservice.controller;

import com.example.customerservice.dto.CustomerDTO;
import com.example.customerservice.dto.SaveCustomerRequest;
import com.example.customerservice.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    @GetMapping
    public ResponseEntity<List<CustomerDTO>> getCustomers(){
        return customerService.getCustomers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> getCustomer(@PathVariable String id){
        return customerService.getCustomer(id);
    }

    @PostMapping
    public ResponseEntity<CustomerDTO> saveCustomer(@RequestBody SaveCustomerRequest saveCustomerRequest){
        return customerService.saveCustomer(saveCustomerRequest);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteCustomer(@PathVariable String id){
        return customerService.deleteCustomer(id);
    }
}

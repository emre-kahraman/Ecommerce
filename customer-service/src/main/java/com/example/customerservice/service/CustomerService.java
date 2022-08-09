package com.example.customerservice.service;

import com.example.customerservice.dto.CustomerDTO;
import com.example.customerservice.dto.CustomerKafka;
import com.example.customerservice.dto.SaveCustomerRequest;
import com.example.customerservice.entity.Customer;
import com.example.customerservice.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final KafkaTemplate<String, CustomerKafka> kafkaTemplate;

    public ResponseEntity<List<CustomerDTO>> getCustomers() {
        List<Customer> customerList = customerRepository.findAll();
        List<CustomerDTO> customerDTOList = new ArrayList<CustomerDTO>();
        customerList.forEach(customer -> customerDTOList.add(convert(customer)));
        return new ResponseEntity<>(customerDTOList, HttpStatus.OK);
    }

    public ResponseEntity<CustomerDTO> getCustomer(String id) {
        Customer customer = customerRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found."));
        CustomerDTO customerDTO = convert(customer);
        return new ResponseEntity<>(customerDTO, HttpStatus.OK);
    }

    public ResponseEntity<CustomerDTO> saveCustomer(SaveCustomerRequest saveCustomerRequest) {
        Customer customer = Customer.builder().name(saveCustomerRequest.getName())
                .lastName(saveCustomerRequest.getLastName())
                .email(saveCustomerRequest.getEmail())
                .password(saveCustomerRequest.getPassword())
                .address(saveCustomerRequest.getAddress()).build();
        Customer savedCustomer = customerRepository.save(customer);
        CustomerKafka customerKafka = new CustomerKafka (savedCustomer.getName(), savedCustomer.getLastName()
                , savedCustomer.getEmail(), savedCustomer.getAddress(), "Create");
        kafkaTemplate.send("customers", savedCustomer.getId(), customerKafka);
        CustomerDTO customerDTO = convert(savedCustomer);
        System.out.println(savedCustomer.getId());
        return new ResponseEntity<>(customerDTO, HttpStatus.CREATED);
    }

    public ResponseEntity<HttpStatus> deleteCustomer(String id) {
        Optional<Customer> customer = customerRepository.findById(id);
        if(customer.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        customerRepository.deleteById(id);
        CustomerKafka customerKafka = new CustomerKafka (customer.get().getName(), customer.get().getLastName(),
                customer.get().getEmail(), customer.get().getAddress(), "Delete");
        kafkaTemplate.send("customers", customer.get().getId(), customerKafka);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public CustomerDTO convert(Customer customer){
        CustomerDTO customerDTO = CustomerDTO.builder()
                .name(customer.getName())
                .lastName(customer.getLastName())
                .email(customer.getEmail())
                .address(customer.getAddress()).build();
        return customerDTO;
    }
}

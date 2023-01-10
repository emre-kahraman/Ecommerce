package com.example.customerservice;

import com.example.customerservice.config.KafkaProducerConfig;
import com.example.customerservice.dto.CustomerDTO;
import com.example.customerservice.dto.CustomerKafka;
import com.example.customerservice.dto.SaveCustomerRequest;
import com.example.customerservice.entity.Customer;
import com.example.customerservice.repository.CustomerRepository;
import com.example.customerservice.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@MockBean({
        KafkaProducerConfig.class
})
public class CustomerIntegrationTests {


    @Autowired
    CustomerService customerService;

    @Autowired
    CustomerRepository customerRepository;

    @MockBean
    KafkaTemplate<String, CustomerKafka> kafkaTemplate;

    @BeforeEach
    public void setup(){
        Customer customer = Customer.builder()
                .id("1")
                .name("test")
                .lastName("test")
                .email("test@gmail.com")
                .address("test").build();
        Customer customer2 = Customer.builder()
                .id("2")
                .name("test2")
                .lastName("test2")
                .email("test2@gmail.com")
                .address("test2").build();
        customerRepository.deleteAll();
        customerRepository.save(customer);
        customerRepository.save(customer2);
    }

    @Test
    public void itShouldReturnAllCustomers(){

        ResponseEntity<List<CustomerDTO>> responseEntity = customerService.getCustomers();

        assertEquals(responseEntity.getBody().size(), 2);
        assertEquals(responseEntity.getBody().get(0).getName(), "test");
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void itShouldReturnCustomerById(){

        ResponseEntity<CustomerDTO> responseEntity = customerService.getCustomer("1");

        assertEquals(responseEntity.getBody().getName(), "test");
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void itShouldSaveCustomer(){
        SaveCustomerRequest saveCustomerRequest = SaveCustomerRequest.builder()
                .name("test3")
                .lastName("test3")
                .email("test3@gmail.com")
                .address("test3").build();

        when(kafkaTemplate.send(eq("customers"), any(), any())).thenReturn(null);

        ResponseEntity<CustomerDTO> responseEntity = customerService.saveCustomer(saveCustomerRequest);

        assertEquals(responseEntity.getBody().getName(), saveCustomerRequest.getName());
        assertEquals(responseEntity.getStatusCode(), HttpStatus.CREATED);
    }

    @Test
    public void itShouldDeleteCustomer(){

        when(kafkaTemplate.send(eq("customers"), any(), any())).thenReturn(null);

        ResponseEntity<HttpStatus> responseEntity = customerService.deleteCustomer("1");

        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        assertEquals(customerRepository.findById("1"), Optional.empty());
    }
}

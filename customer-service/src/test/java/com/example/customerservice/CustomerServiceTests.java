package com.example.customerservice;

import com.example.customerservice.dto.CustomerDTO;
import com.example.customerservice.dto.CustomerKafka;
import com.example.customerservice.dto.SaveCustomerRequest;
import com.example.customerservice.entity.Customer;
import com.example.customerservice.repository.CustomerRepository;
import com.example.customerservice.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTests {

    @InjectMocks
    CustomerService customerService;

    @Mock
    CustomerRepository customerRepository;

    @Mock
    KafkaTemplate<String, CustomerKafka> kafkaTemplate;

    @Test
    public void itShouldReturnAllCustomers(){
        Customer customer = Customer.builder()
                .name("test")
                .lastName("test")
                .email("test@gmail.com")
                .address("test").build();
        Customer customer2 = Customer.builder()
                .name("test")
                .lastName("test")
                .email("test@gmail.com")
                .address("test").build();
        List<Customer> customerList = new ArrayList<>(List.of(customer, customer2));

        when(customerRepository.findAll()).thenReturn(customerList);

        ResponseEntity<List<CustomerDTO>> responseEntity = customerService.getCustomers();

        verify(customerRepository).findAll();
        assertEquals(responseEntity.getBody().size(), customerList.size());
        assertEquals(responseEntity.getBody().get(0).getName(), customer.getName());
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void itShouldReturnCustomerById(){
        Customer customer = Customer.builder()
                .id("1")
                .name("test")
                .lastName("test")
                .email("test@gmail.com")
                .address("test").build();

        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));

        ResponseEntity<CustomerDTO> responseEntity = customerService.getCustomer(customer.getId());

        verify(customerRepository).findById(customer.getId());
        assertEquals(responseEntity.getBody().getName(), customer.getName());
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void itShouldSaveCustomer(){
        SaveCustomerRequest saveCustomerRequest = SaveCustomerRequest.builder()
                .name("test")
                .lastName("test")
                .email("test@gmail.com")
                .address("test").build();
        Customer customer = Customer.builder()
                .name("test")
                .lastName("test")
                .email("test@gmail.com")
                .address("test").build();

        when(customerRepository.save(any())).thenReturn(customer);
        when(kafkaTemplate.send(eq("customers"), any(), any())).thenReturn(null);

        ResponseEntity<CustomerDTO> responseEntity = customerService.saveCustomer(saveCustomerRequest);

        verify(customerRepository).save(any());
        assertEquals(responseEntity.getBody().getName(), customer.getName());
        assertEquals(responseEntity.getStatusCode(), HttpStatus.CREATED);
    }

    @Test
    public void itShouldUpdateCustomer(){
        SaveCustomerRequest saveCustomerRequest = SaveCustomerRequest.builder()
                .name("test")
                .lastName("test")
                .email("test@gmail.com")
                .address("test").build();
        Customer customer = Customer.builder()
                .id("1")
                .name("test2")
                .lastName("test2")
                .email("test2@gmail.com")
                .address("test2").build();

        when(customerRepository.save(any())).thenReturn(customer);
        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        when(kafkaTemplate.send(eq("customers"), any(), any())).thenReturn(null);

        ResponseEntity<CustomerDTO> responseEntity = customerService.updateCustomer(customer.getId(), saveCustomerRequest);

        verify(customerRepository).save(any());
        assertEquals(responseEntity.getBody().getName(), customer.getName());
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void itShouldDeleteCustomer(){

        Customer customer = Customer.builder()
                .id("1")
                .name("test")
                .lastName("test")
                .email("test@gmail.com")
                .address("test").build();

        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        doNothing().when(customerRepository).deleteById(customer.getId());
        when(kafkaTemplate.send(eq("customers"), any(), any())).thenReturn(null);

        ResponseEntity<HttpStatus> responseEntity = customerService.deleteCustomer(customer.getId());

        verify(customerRepository).deleteById(customer.getId());
        verify(customerRepository).findById(customer.getId());
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void itShouldConvertCustomerToCustomerDTO(){
        Customer customer = Customer.builder()
                .name("test")
                .lastName("test")
                .email("test@gmail.com")
                .address("test").build();

        CustomerDTO customerDTO = customerService.convert(customer);

        assertEquals(customer.getName(), customerDTO.getName());
        assertEquals(customer.getLastName(), customerDTO.getLastName());
        assertEquals(customer.getEmail(), customerDTO.getEmail());
        assertEquals(customer.getAddress(), customerDTO.getAddress());
    }
}

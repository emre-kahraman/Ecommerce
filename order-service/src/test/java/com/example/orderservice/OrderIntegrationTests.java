package com.example.orderservice;

import com.example.cartservice.dto.CreateOrderRequest;
import com.example.orderservice.dto.CreateEmailRequest;
import com.example.orderservice.dto.OrderDTO;
import com.example.orderservice.entity.Order;
import com.example.orderservice.repository.OrderRepository;
import com.example.orderservice.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
public class OrderIntegrationTests {

    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    KafkaTemplate<String, CreateEmailRequest> kafkaTemplate;

    @BeforeEach
    public void setup(){
        Order order = Order.builder().id("1").userId("1").userName("test").userLastName("test").email("test@gmail.com").address("test")
                .cartItems(new HashSet<>()).date(new Date()).build();
        Order order2 = Order.builder().id("2").userId("2").userName("test2").userLastName("test2").email("test2@gmail.com").address("test2")
                .cartItems(new HashSet<>()).date(new Date()).build();
        orderRepository.deleteAll();
        orderRepository.save(order);
        orderRepository.save(order2);
    }

    @Test
    public void itShouldGetAllOrders(){

        ResponseEntity<List<OrderDTO>> responseEntity = orderService.getOrders();

        assertEquals(responseEntity.getBody().size(), 2);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void itShouldGetOrderById(){

        ResponseEntity<OrderDTO> responseEntity = orderService.getOrderById("1");

        assertEquals(responseEntity.getBody().getUserId(), "1");
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void itShouldGetAllOrdersByUserId(){

        ResponseEntity<List<OrderDTO>> responseEntity = orderService.getOrdersByUserId("1");

        assertEquals(responseEntity.getBody().size(), 1);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void itShouldCreateOrder(){

        CreateOrderRequest createOrderRequest = new CreateOrderRequest("1", "test"
                , "test", "test@gmail.com", "test", new HashSet<>(), BigDecimal.valueOf(0));

        Order savedOrder = orderService.createOrder(createOrderRequest);

        assertEquals(savedOrder.getUserId(), createOrderRequest.getUserId());
        assertEquals(savedOrder.getUserName(), createOrderRequest.getUserName());
    }

}

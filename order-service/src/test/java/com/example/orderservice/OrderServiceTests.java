package com.example.orderservice;

import com.example.orderservice.entity.Order;
import com.example.orderservice.repository.OrderRepository;
import com.example.orderservice.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTests {

    @InjectMocks
    OrderService orderService;

    @Mock
    OrderRepository orderRepository;

    @Test
    public void itShouldGetAllOrders(){
        Order order = Order.builder().userId("1").userName("test").userLastName("test").email("test@gmail.com").address("test")
                .cartItems(new HashSet<>()).date(new Date()).build();
        Order order2 = Order.builder().userId("2").userName("test2").userLastName("test2").email("test2@gmail.com").address("test2")
                .cartItems(new HashSet<>()).date(new Date()).build();
        List<Order> orderList = List.of(order, order2);

        when(orderRepository.findAll()).thenReturn(orderList);

        ResponseEntity<List<OrderDTO>> responseEntity = orderService.getOrders();

        assertEquals(responseEntity.getBody().size(), orderList.size());
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void itShouldGetAllOrdersByUserId(){
        Order order = Order.builder().userId("1").userName("test").userLastName("test").email("test@gmail.com").address("test")
                .cartItems(new HashSet<>()).date(new Date()).build();
        Order order2 = Order.builder().userId("2").userName("test2").userLastName("test2").email("test2@gmail.com").address("test2")
                .cartItems(new HashSet<>()).date(new Date()).build();
        List<Order> orderList = List.of(order);

        when(orderRepository.getOrdersByUserId(order.getUserId())).thenReturn(orderList);

        ResponseEntity<List<OrderDTO>> responseEntity = orderService.getOrdersByUserId(order.getUserId());

        assertEquals(responseEntity.getBody().size(), 1);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
    }
}

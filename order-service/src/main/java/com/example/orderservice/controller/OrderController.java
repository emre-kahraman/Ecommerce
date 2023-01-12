package com.example.orderservice.controller;

import com.example.orderservice.dto.OrderDTO;
import com.example.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<List<OrderDTO>> getOrders(){
        return orderService.getOrders();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrdersById(String id){
        return orderService.getOrderById(id);
    }

    @GetMapping("/getOrdersByUserId/{userId}")
    public ResponseEntity<List<OrderDTO>> getOrdersByUserId(String userId){
        return orderService.getOrdersByUserId(userId);
    }
}

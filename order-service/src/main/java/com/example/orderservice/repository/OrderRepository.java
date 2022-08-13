package com.example.orderservice.repository;

import com.example.orderservice.entity.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface OrderRepository extends MongoRepository<Order, String> {

    List<Order> getOrdersByUserId(String userId);
}

package com.example.orderservice.service;

import com.example.cartservice.dto.CreateOrderRequest;
import com.example.orderservice.dto.CreateEmailRequest;
import com.example.orderservice.dto.OrderDTO;
import com.example.orderservice.entity.Order;
import com.example.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, CreateEmailRequest> kafkaTemplate;
    public ResponseEntity<List<OrderDTO>> getOrders() {
        List<Order> orderList = orderRepository.findAll();
        List<OrderDTO> orderDTOList = new ArrayList<>();
        orderList.forEach(order -> orderDTOList.add(convert(order)));
        return new ResponseEntity<>(orderDTOList, HttpStatus.OK);
    }

    public ResponseEntity<OrderDTO> getOrderById(String id) {
        Optional<Order> order = orderRepository.findById(id);
        if(order.isEmpty())
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        OrderDTO orderDTO = convert(order.get());
        return new ResponseEntity<>(orderDTO, HttpStatus.OK);
    }

    public ResponseEntity<List<OrderDTO>> getOrdersByUserId(String userId) {
        List<Order> orderList = orderRepository.getOrdersByUserId(userId);
        List<OrderDTO> orderDTOList = new ArrayList<>();
        orderList.forEach(order -> orderDTOList.add(convert(order)));
        return new ResponseEntity<>(orderDTOList, HttpStatus.OK);
    }

    @KafkaListener(topics = "orders", groupId = "order")
    public void orderListener(@Payload CreateOrderRequest createOrderRequest){
        createOrder(createOrderRequest);
    }

    public Order createOrder(CreateOrderRequest createOrderRequest){
        Order order = Order.builder().userId(createOrderRequest.getUserId())
                .userName(createOrderRequest.getUserName())
                .userLastName(createOrderRequest.getUserLastName())
                .email(createOrderRequest.getEmail())
                .address(createOrderRequest.getAddress())
                .cartItems(createOrderRequest.getCartItems())
                .totalPrice(createOrderRequest.getTotalPrice())
                .date(new Date()).build();
        Order savedOrder = orderRepository.save(order);
        createEmailRequest(savedOrder);
        return savedOrder;
    }

    public CreateEmailRequest createEmailRequest(Order order){
        CreateEmailRequest createEmailRequest = CreateEmailRequest.builder().orderId(order.getId())
                .userName(order.getUserName())
                .userLastName(order.getUserLastName())
                .email(order.getEmail())
                .address(order.getAddress())
                .cartItems(order.getCartItems())
                .totalPrice(order.getTotalPrice())
                .date(order.getDate()).build();
        kafkaTemplate.send("emails", createEmailRequest.getOrderId(), createEmailRequest);
        return createEmailRequest;
    }

    public OrderDTO convert(Order order){
        return OrderDTO.builder().userId(order.getUserId())
                .userName(order.getUserName())
                .userLastName(order.getUserLastName())
                .email(order.getEmail())
                .address(order.getAddress())
                .cartItems(order.getCartItems())
                .totalPrice(order.getTotalPrice())
                .date(order.getDate()).build();
    }
}

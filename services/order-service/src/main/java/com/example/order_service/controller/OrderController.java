package com.example.order_service.controller;

import com.example.order_service.entity.Order;
import com.example.order_service.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/customer/{username}")
    public List<Order> getOrdersByCustomer(@PathVariable String username) {
        return orderRepository.findByUsernameOrderByPlacedAtDesc(username);
    }
}


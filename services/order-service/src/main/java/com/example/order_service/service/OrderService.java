package com.example.order_service.service;

import com.example.booking_service.event.BookingEvent;
import com.example.order_service.entity.Order;
import com.example.order_service.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderService {
    private OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @KafkaListener(topics = "booking", groupId = "order-service")
    public void orderEvent(BookingEvent bookingEvent) {
        log.info("Received order event: {}", bookingEvent);

        // Create Order object for DB
        Order order = createOrder(bookingEvent);
        orderRepository.saveAndFlush(order);

        log.info("Inventory updated for event {} with {} tickets", order.getEventId(), order.getTicketCount());
    }

    private Order createOrder(BookingEvent bookingEvent) {
        return Order.builder()
                .username(bookingEvent.getUsername())
                .eventId(bookingEvent.getEventId())
                .ticketCount(bookingEvent.getTicketCount())
                .totalPrice(bookingEvent.getTotalPrice())
                .build();
    }
}

package com.example.booking_service.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.booking_service.client.InventoryServiceClient;
import com.example.booking_service.entity.Customer;
import com.example.booking_service.event.BookingEvent;
import com.example.booking_service.repository.CustomerRepository;
import com.example.booking_service.request.BookingRequest;
import com.example.booking_service.response.BookingResponse;
import com.example.booking_service.response.InventoryResponse;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BookingService {

    private final CustomerRepository customerRepository;
    private final InventoryServiceClient inventoryServiceClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public BookingService(final CustomerRepository customerRepository,
                          final InventoryServiceClient inventoryServiceClient,
                          final KafkaTemplate<String, Object> kafkaTemplate) {
        this.customerRepository = customerRepository;
        this.inventoryServiceClient = inventoryServiceClient;
        this.kafkaTemplate = kafkaTemplate;
    }

    public Customer findCustomerByUsername(final String username) {
        return customerRepository.findByUsername(username).orElse(null);
    }

    public Boolean checkUsernameExist(final String username) {
        return customerRepository.existsByUsername(username);
    }

    @Transactional
    public Customer createCustomer(final Customer customer) {
        // Set default role if not provided
        if (customer.getRole() == null || customer.getRole().isEmpty()) {
            customer.setRole("CUSTOMER");
        }
        Customer savedCustomer = customerRepository.save(customer);
        return savedCustomer;
    }

    @Transactional
    public BookingResponse createBooking(final BookingRequest request) {
        // Check if user exist
        final Customer customer = customerRepository.findById(request.getUserId()).orElse(null);
        if (customer == null) {
            throw new RuntimeException("Customer not found!");
        }

        // Call Inventory Service to check and book tickets atomically
        final InventoryResponse inventoryResponse = inventoryServiceClient.checkAndBookInventory(request.getEventId(), request.getTicketCount());
        
        if (inventoryResponse == null) {
            throw new RuntimeException("Not enough tickets in inventory or event not found!");
        }

        // Create booking event
        final BookingEvent bookingEvent = createBookingEvent(request, customer, inventoryResponse);

        // Send booking to order-service via Kafka
        kafkaTemplate.send("booking", bookingEvent);
        log.info("Booking sent to kafka for order processing: {}", bookingEvent);

        return BookingResponse.builder()
                .userId(bookingEvent.getUserId())
                .eventId(bookingEvent.getEventId())
                .ticketCount(bookingEvent.getTicketCount())
                .totalPrice(bookingEvent.getTotalPrice())
                .build();
    }

    private BookingEvent createBookingEvent(final BookingRequest request,
                                            final Customer customer,
                                            final InventoryResponse inventoryResponse) {
        return BookingEvent.builder()
                .userId(customer.getId())
                .eventId(request.getEventId())
                .ticketCount(request.getTicketCount())
                .totalPrice(inventoryResponse.getTicketPrice().multiply(BigDecimal.valueOf(request.getTicketCount())))
                .build();
    }
}

package com.example.booking_service.controller;

import com.example.booking_service.entity.Customer;
import com.example.booking_service.request.BookingRequest;
import com.example.booking_service.request.LoginRequest;
import com.example.booking_service.response.BookingResponse;
import com.example.booking_service.service.BookingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@RequestBody Customer customer) {
        String username = customer.getUsername();

        if(bookingService.checkUsernameExist(username)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        Customer response = bookingService.createCustomer(customer);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        Customer customer = bookingService.findCustomerByUsername(username);
        if (customer == null) {
            return ResponseEntity.notFound().build();
        }

        if (!password.equals(customer.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(customer);
    }

    @PostMapping(consumes = "application/json", produces = "application/json", path = "/booking")
    public BookingResponse createBooking(@RequestBody BookingRequest request) {
        return bookingService.createBooking(request);
    }
}

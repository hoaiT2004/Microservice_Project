package com.example.notificationservice.listener;

import com.example.notificationservice.event.BookingEvent;
import com.example.notificationservice.event.RegistrationEvent;
import com.example.notificationservice.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@Slf4j
public class NotificationEventListener {

    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    @Autowired
    public NotificationEventListener(EmailService emailService, ObjectMapper objectMapper) {
        this.emailService = emailService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "register_success", groupId = "notification-group")
    public void handleRegistrationEvent(String message) {
        try {
            log.info("Received registration event: {}", message);
            RegistrationEvent event = objectMapper.readValue(message, RegistrationEvent.class);
            
            if (event.getEmail() != null && !event.getEmail().isEmpty()) {
                String subject = "Đăng ký tài khoản thành công";
                String text = String.format("Chào %s,\n\nChúc mừng bạn đã đăng ký tài khoản thành công.\n\nTrân trọng.", 
                                            event.getUsername() != null ? event.getUsername() : "Bạn");
                
                emailService.sendSimpleEmail(event.getEmail(), subject, text);
            } else {
                log.warn("Cannot send registration email. User email is missing for user ID: {}", event.getUserId());
            }
        } catch (Exception e) {
            log.error("Error processing registration event: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "buy_ticket_success", groupId = "notification-group")
    public void handleBookingEvent(String message) {
        try {
            log.info("Received buy_ticket_success event: {}", message);
            BookingEvent event = objectMapper.readValue(message, BookingEvent.class);
            
            // To send a proper email, we need the user's email address.
            // Ideally, this should be included in the BookingEvent published by booking-service.
            String email = event.getUserEmail();
            
            if (email != null && !email.isEmpty()) {
                String subject = "Xác nhận đặt vé thành công";
                String text = String.format("Cảm ơn bạn đã đặt vé.\n\nThông tin vé:\n- Mã sự kiện: %d\n- Số lượng vé: %d\n- Tổng tiền: %s\n\nChúc bạn có trải nghiệm tuyệt vời!", 
                                            event.getEventId(), event.getTicketCount(), event.getTotalPrice());
                
                emailService.sendSimpleEmail(email, subject, text);
            } else {
                log.warn("Cannot send booking email. User email is missing in BookingEvent for user ID: {}", event.getUserId());
            }
        } catch (Exception e) {
            log.error("Error processing booking event: {}", e.getMessage(), e);
        }
    }
}

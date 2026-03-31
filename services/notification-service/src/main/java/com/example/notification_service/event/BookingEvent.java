package com.example.notification_service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingEvent {
    private Long userId;
    private Long eventId;
    private Long ticketCount;
    private BigDecimal totalPrice;
    // We might need more details like event name, user email etc.
    // For now, we assume the notification service might need to call other services to get these details if needed.
    // Or, ideally, these details should be included in the event itself.
    // Let's add user's email here for simplicity, assuming booking-service will provide it.
    private String userEmail; 
}

package com.example.inventory_service.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventInventoryRequest {
    private String name;
    private Long totalCapacity;
    private BigDecimal ticketPrice;
    private Long venueId;
}

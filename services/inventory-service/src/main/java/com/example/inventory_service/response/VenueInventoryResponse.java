package com.example.inventory_service.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VenueInventoryResponse {
    private Long venueId;
    private String venueName;
    private String venueAddress;
    private Long totalCapacity;
}

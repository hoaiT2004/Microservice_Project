package com.example.booking_service.client;

import com.example.booking_service.response.InventoryResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.http.ResponseEntity;

@Service
public class InventoryServiceClient {

    @Value("${inventory.service.url}")
    private String inventoryServiceUrl;

    public InventoryResponse checkAndBookInventory(final Long eventId, final Long ticketCount) {
        final RestTemplate restTemplate = new RestTemplate();
        // Changed to POST and pass ticketsToBook as query param
        String url = inventoryServiceUrl + "/event/" + eventId + "?ticketsToBook=" + ticketCount;
        
        try {
             ResponseEntity<InventoryResponse> response = restTemplate.postForEntity(url, null, InventoryResponse.class);
             if (response.getStatusCode().is2xxSuccessful()) {
                 return response.getBody();
             }
        } catch (HttpClientErrorException.BadRequest e) {
             // Handle 400 Bad Request which we set in InventoryController when not enough tickets
             return null;
        } catch (Exception e) {
             // Other errors
             return null;
        }
        return null;
    }
}

package com.example.inventory_service.controller;

import com.example.inventory_service.entity.Event;
import com.example.inventory_service.entity.Venue;
import com.example.inventory_service.request.EventInventoryRequest;
import com.example.inventory_service.response.EventInventoryResponse;
import com.example.inventory_service.response.VenueInventoryResponse;
import com.example.inventory_service.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class InventoryController {

    private InventoryService inventoryService;

    @Autowired
    public InventoryController(final InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/inventory/venues")
    public List<VenueInventoryResponse> inventoryGetAllVenues() {
        return inventoryService.getAllVenues();
    }

    @GetMapping("/inventory/venue/{venueId}")
    public @ResponseBody VenueInventoryResponse inventoryByVenueId(@PathVariable("venueId") Long venueId) {
        return inventoryService.getVenueInformation(venueId);
    }

    @PostMapping("/inventory/venue")
    public ResponseEntity<?> inventoryCreateVenue(@RequestBody Venue venue) {
        Venue response = inventoryService.createVenue(venue);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/inventory/events")
    public @ResponseBody List<EventInventoryResponse> inventoryGetAllEvents() {
        return inventoryService.getAllEvents();
    }

    // Changed to POST to allow updating state.
    // Receives ticketsToBook as a query parameter.
    @PostMapping("/inventory/event/{eventId}")
    public ResponseEntity<?> inventoryForEvent(@PathVariable("eventId") Long eventId, 
                                               @RequestParam(name = "ticketsToBook", defaultValue = "0") Long ticketsToBook) {
        
        // If ticketsToBook is 0, just return the inventory info (fallback to original logic)
        if (ticketsToBook == 0) {
             return ResponseEntity.ok(inventoryService.getEventInventory(eventId));
        }

        // Logic to check and decrease capacity simultaneously
        boolean success = inventoryService.decreaseEventCapacity(eventId, ticketsToBook);
        
        if (success) {
            // Return updated inventory info
            return ResponseEntity.ok(inventoryService.getEventInventory(eventId));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not enough tickets or event not found");
        }
    }

    @PostMapping("/inventory/event")
    public ResponseEntity<?> inventoryCreateEvent(@RequestBody EventInventoryRequest request) {
        Venue venue = inventoryService.getVenueById(request.getVenueId());

        Event event = new Event();
        event.setName(request.getName());
        event.setTotalCapacity(request.getTotalCapacity());
        event.setLeftCapacity(request.getTotalCapacity());
        event.setTicketPrice(request.getTicketPrice());
        event.setVenue(venue);

        Event response = inventoryService.createEvent(event);
        return ResponseEntity.ok(response);
    }


    @PutMapping("/inventory/event/{eventId}/capacity/{capacity}")
    public ResponseEntity<Void> updateEventCapacity(@PathVariable("eventId") Long eventId,
                                                    @PathVariable("capacity") Long ticketsBooked) {
        // Now delegating to the safe method, but this endpoint might be obsolete if inventoryForEvent handles it
        boolean success = inventoryService.decreaseEventCapacity(eventId, ticketsBooked);
        if (success) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}

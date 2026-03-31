package com.example.inventory_service.service;

import com.example.inventory_service.entity.Event;
import com.example.inventory_service.entity.Venue;
import com.example.inventory_service.repository.EventRepository;
import com.example.inventory_service.repository.VenueRepository;
import com.example.inventory_service.response.EventInventoryResponse;
import com.example.inventory_service.response.VenueInventoryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class InventoryService {

    private final EventRepository eventRepository;
    private final VenueRepository venueRepository;

    @Autowired
    public InventoryService(final EventRepository eventRepository, final VenueRepository venueRepository) {
        this.eventRepository = eventRepository;
        this.venueRepository = venueRepository;
    }

    public List<VenueInventoryResponse> getAllVenues() {
        final List<Venue> venues = venueRepository.findAll();

        return venues.stream().map(venue -> VenueInventoryResponse.builder()
                .venueId(venue.getId())
                .venueName(venue.getName())
                .venueAddress(venue.getAddress())
                .totalCapacity(venue.getTotalCapacity())
                .build()).collect(Collectors.toList());
    }

    public VenueInventoryResponse getVenueInformation(final Long venueId) {
        final Venue venue = venueRepository.findById(venueId).orElse(null);

        return VenueInventoryResponse.builder()
                .venueId(venue.getId())
                .venueName(venue.getName())
                .venueAddress(venue.getAddress())
                .totalCapacity(venue.getTotalCapacity())
                .build();
    }

    public Venue getVenueById(final Long venueId) {
        return venueRepository.findById(venueId).orElse(null);
    }

    public Venue createVenue(final Venue venue) {
        return venueRepository.save(venue);
    }

    public List<EventInventoryResponse> getAllEvents() {
        final List<Event> events = eventRepository.findAll();

        return events.stream().map(event -> EventInventoryResponse.builder()
                .event(event.getName())
                .capacity(event.getLeftCapacity())
                .venue(event.getVenue())
                .eventId(event.getId())
                .ticketPrice(event.getTicketPrice())
                .build()).collect(Collectors.toList());
    }

    public EventInventoryResponse getEventInventory(final Long eventId) {
        final Event event = eventRepository.findById(eventId).orElse(null);

        return EventInventoryResponse.builder()
                .event(event.getName())
                .capacity(event.getLeftCapacity())
                .venue(event.getVenue())
                .ticketPrice(event.getTicketPrice())
                .eventId(event.getId())
                .build();
    }

    public Event createEvent(final Event event) {
        return eventRepository.save(event);
    }

    @Transactional
    public boolean decreaseEventCapacity(final Long eventId, final Long ticketsToBook) {
        int updatedRows = eventRepository.decreaseCapacity(eventId, ticketsToBook);
        if (updatedRows > 0) {
            log.info("Successfully decreased capacity for event id {} by {} tickets.", eventId, ticketsToBook);
            return true;
        } else {
            log.warn("Failed to decrease capacity for event id {}. Not enough tickets or event not found.", eventId);
            return false;
        }
    }

    @Deprecated // This method is not safe for concurrent requests. Use decreaseEventCapacity instead.
    public void updateEventCapacity(final Long eventId, final Long ticketsBooked) {
        final Event event = eventRepository.findById(eventId).orElse(null);
        if (event != null) {
            event.setLeftCapacity(event.getLeftCapacity() - ticketsBooked);
            eventRepository.saveAndFlush(event);
            log.info("Updated event capacity for event id {} with tickets booked {}", eventId, ticketsBooked);
        }
    }
}

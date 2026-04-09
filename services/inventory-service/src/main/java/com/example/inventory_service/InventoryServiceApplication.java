package com.example.inventory_service;

import com.example.inventory_service.entity.Event;
import com.example.inventory_service.entity.Venue;
import com.example.inventory_service.repository.EventRepository;
import com.example.inventory_service.repository.VenueRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.math.BigDecimal;

@SpringBootApplication
@Slf4j
public class InventoryServiceApplication {

	@Autowired
	private EventRepository eventRepository;

	@Autowired
	private VenueRepository venueRepository;

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}

	@PostConstruct
	public void seedInventory() {
		Venue grandHall = new Venue();
		grandHall.setName("Grand Hall");
		grandHall.setAddress("123 Nguyen Trai, Ha Noi");
		grandHall.setTotalCapacity(5000L);
		grandHall = venueRepository.save(grandHall);

		Venue riversideCenter = new Venue();
		riversideCenter.setName("Riverside Center");
		riversideCenter.setAddress("88 Bach Dang, Da Nang");
		riversideCenter.setTotalCapacity(3500L);
		riversideCenter = venueRepository.save(riversideCenter);

		Event acousticNight = new Event();
		acousticNight.setName("Acoustic Night");
		acousticNight.setTotalCapacity(1200L);
		acousticNight.setLeftCapacity(1200L);
		acousticNight.setTicketPrice(new BigDecimal("350000"));
		acousticNight.setVenue(grandHall);
		eventRepository.save(acousticNight);

		Event techSummit = new Event();
		techSummit.setName("Tech Summit 2026");
		techSummit.setTotalCapacity(1800L);
		techSummit.setLeftCapacity(1800L);
		techSummit.setTicketPrice(new BigDecimal("500000"));
		techSummit.setVenue(grandHall);
		eventRepository.save(techSummit);

		Event startupExpo = new Event();
		startupExpo.setName("Startup Expo");
		startupExpo.setTotalCapacity(1500L);
		startupExpo.setLeftCapacity(1500L);
		startupExpo.setTicketPrice(new BigDecimal("275000"));
		startupExpo.setVenue(grandHall);
		eventRepository.save(startupExpo);

		Event summerFestival = new Event();
		summerFestival.setName("Summer Festival");
		summerFestival.setTotalCapacity(2000L);
		summerFestival.setLeftCapacity(2000L);
		summerFestival.setTicketPrice(new BigDecimal("450000"));
		summerFestival.setVenue(grandHall);
		eventRepository.save(summerFestival);

		Event classicalEvening = new Event();
		classicalEvening.setName("Classical Evening");
		classicalEvening.setTotalCapacity(1000L);
		classicalEvening.setLeftCapacity(1000L);
		classicalEvening.setTicketPrice(new BigDecimal("400000"));
		classicalEvening.setVenue(grandHall);
		eventRepository.save(classicalEvening);

		Event foodCarnival = new Event();
		foodCarnival.setName("Food Carnival");
		foodCarnival.setTotalCapacity(900L);
		foodCarnival.setLeftCapacity(900L);
		foodCarnival.setTicketPrice(new BigDecimal("150000"));
		foodCarnival.setVenue(riversideCenter);
		eventRepository.save(foodCarnival);

		Event indieMusicLive = new Event();
		indieMusicLive.setName("Indie Music Live");
		indieMusicLive.setTotalCapacity(1100L);
		indieMusicLive.setLeftCapacity(1100L);
		indieMusicLive.setTicketPrice(new BigDecimal("320000"));
		indieMusicLive.setVenue(riversideCenter);
		eventRepository.save(indieMusicLive);

		Event businessNetworkingDay = new Event();
		businessNetworkingDay.setName("Business Networking Day");
		businessNetworkingDay.setTotalCapacity(800L);
		businessNetworkingDay.setLeftCapacity(800L);
		businessNetworkingDay.setTicketPrice(new BigDecimal("220000"));
		businessNetworkingDay.setVenue(riversideCenter);
		eventRepository.save(businessNetworkingDay);

		Event designConference = new Event();
		designConference.setName("Design Conference");
		designConference.setTotalCapacity(950L);
		designConference.setLeftCapacity(950L);
		designConference.setTicketPrice(new BigDecimal("380000"));
		designConference.setVenue(riversideCenter);
		eventRepository.save(designConference);

		Event comedyWeekend = new Event();
		comedyWeekend.setName("Comedy Weekend");
		comedyWeekend.setTotalCapacity(1000L);
		comedyWeekend.setLeftCapacity(1000L);
		comedyWeekend.setTicketPrice(new BigDecimal("280000"));
		comedyWeekend.setVenue(riversideCenter);
		eventRepository.save(comedyWeekend);

		log.info("Inventory startup seeding completed. Current totals -> venues: {}, events: {}",
				venueRepository.count(), eventRepository.count());
	}
}

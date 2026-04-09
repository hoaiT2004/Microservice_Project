package com.example.inventory_service.repository;

import com.example.inventory_service.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VenueRepository extends JpaRepository<Venue, Long> {

    @Query("SELECT DISTINCT v FROM Venue v LEFT JOIN FETCH v.events e " +
            "WHERE (:venue = '' OR LOWER(v.name) LIKE LOWER(CONCAT(:venue, '%'))) " +
            "AND (:event = '' OR LOWER(e.name) LIKE LOWER(CONCAT(:event, '%')))")
    List<Venue> searchVenuesWithEvents(@Param("venue") String venue, @Param("event") String event);
}

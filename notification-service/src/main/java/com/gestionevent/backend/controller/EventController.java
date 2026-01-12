package com.gestionevent.backend.controller;

import com.gestionevent.backend.dto.response.ApiResponse;
import com.gestionevent.backend.model.Event;
import com.gestionevent.backend.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur pour gérer les événements
 * Microservice Events - API pour le frontend
 */
@RestController
@RequestMapping("/api/events")
public class EventController {
    
    @Autowired
    private EventService eventService;
    
    /**
     * Récupère les 20 derniers événements
     * GET /api/events/latest
     */
    @GetMapping("/latest")
    public ResponseEntity<ApiResponse<Page<Event>>> getLatestEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Event> events = eventService.getLatestEvents(pageable);
        
        ApiResponse<Page<Event>> response = ApiResponse.success(
                "20 derniers événements récupérés", 
                events
        );
        return ResponseEntity.ok(response);
    }
    
    /**
     * Récupère tous les événements avec pagination
     * GET /api/events
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<Event>>> getAllEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Event> events = eventService.getEvents(pageable);
        
        ApiResponse<Page<Event>> response = ApiResponse.success(
                "Événements récupérés", 
                events
        );
        return ResponseEntity.ok(response);
    }
    
    /**
     * Récupère un événement par ID
     * GET /api/events/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Event>> getEventById(@PathVariable Long id) {
        Event event = eventService.getEventById(id);
        
        if (event == null) {
            ApiResponse<Event> response = ApiResponse.error("Événement non trouvé");
            return ResponseEntity.notFound().build();
        }
        
        ApiResponse<Event> response = ApiResponse.success("Événement récupéré", event);
        return ResponseEntity.ok(response);
    }
}


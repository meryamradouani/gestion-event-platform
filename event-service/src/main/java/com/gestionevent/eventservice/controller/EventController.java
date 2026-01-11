package com.gestionevent.eventservice.controller;

import com.gestionevent.eventservice.dto.CreateEventRequest;
import com.gestionevent.eventservice.dto.EventResponse;
import com.gestionevent.eventservice.model.Event;
import com.gestionevent.eventservice.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/events")
@Tag(name = "Événements", description = "API de gestion des événements")
public class EventController {

    @Autowired
    private EventService eventService;

    @Operation(summary = "Récupérer tous les événements", description = "Retourne la liste de tous les événements")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @GetMapping
    public ResponseEntity<List<EventResponse>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @Operation(summary = "Récupérer un événement par ID")
    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEventById(
            @Parameter(description = "ID de l'événement") @PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    @Operation(summary = "Créer un nouvel événement", description = "Créer un événement avec une image optionnelle. Utiliser multipart/form-data. 'event' est le JSON, 'image' est le fichier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Événement créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EventResponse> createEvent(
            @RequestPart("event") @Valid CreateEventRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {

        // Pour les tests, si userId n'est pas fourni, utiliser 1 par défaut
        if (userId == null) {
            userId = 1L;
        }

        EventResponse createdEvent = eventService.createEventWithImage(request, image, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
    }

    @Operation(summary = "Récupérer l'image de l'événement")
    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getEventImage(@PathVariable Long id) {
        return eventService.getEventImage(id)
                .map(image -> ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(image.getContentType()))
                        .body(image.getData()))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Mettre à jour un événement")
    @PutMapping(value = "/{id}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<EventResponse> updateEvent(
            @Parameter(description = "ID de l'événement") @PathVariable Long id,
            @RequestPart("event") @Valid CreateEventRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) throws IOException {

        // Pour les tests, si userId n'est pas fourni, utiliser 1 par défaut
        if (userId == null) {
            userId = 1L;
        }

        return ResponseEntity.ok(eventService.updateEventWithImage(id, request, image, userId));
    }

    @Operation(summary = "Supprimer un événement")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(
            @Parameter(description = "ID de l'événement") @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {

        if (userId == null) {
            userId = 1L;
        }
        eventService.deleteEvent(id, userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Récupérer les événements à venir")
    @GetMapping("/upcoming")
    public ResponseEntity<List<EventResponse>> getUpcomingEvents() {
        return ResponseEntity.ok(eventService.getUpcomingEvents());
    }

    @Operation(summary = "Récupérer les événements d'un organisateur")
    @GetMapping("/organizer/{organizerId}")
    public ResponseEntity<List<EventResponse>> getEventsByOrganizer(
            @Parameter(description = "ID de l'organisateur") @PathVariable Long organizerId) {
        return ResponseEntity.ok(eventService.getEventsByOrganizer(organizerId));
    }

    @Operation(summary = "Récupérer les événements actifs")
    @GetMapping("/active")
    public ResponseEntity<List<EventResponse>> getActiveEvents() {
        return ResponseEntity.ok(eventService.getActiveEvents());
    }

    @Operation(summary = "Mettre à jour seulement l'image d'un événement")
    @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EventResponse> updateEventImageOnly(
            @PathVariable Long id,
            @RequestParam("image") MultipartFile imageFile,
            @RequestHeader("X-User-Id") Long userId) throws IOException {

        EventResponse updatedEvent = eventService.updateEventImage(id, imageFile, userId);
        return ResponseEntity.ok(updatedEvent);
    }
}

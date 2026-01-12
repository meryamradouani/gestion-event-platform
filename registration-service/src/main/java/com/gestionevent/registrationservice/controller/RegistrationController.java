package com.gestionevent.registrationservice.controller;

import com.gestionevent.registrationservice.model.EventRegistration;
import com.gestionevent.registrationservice.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/registrations")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    // 1. S'inscrire à un événement
    @PostMapping("/events/{eventId}/users/{userId}")
    public ResponseEntity<EventRegistration> register(
            @PathVariable Long eventId,
            @PathVariable Long userId,
            @RequestParam(required = false) Integer maxParticipants) {

        EventRegistration registration = registrationService.registerUserToEvent(userId, eventId, maxParticipants);
        return ResponseEntity.ok(registration);
    }

    // 2. Se désinscrire d'un événement
    @DeleteMapping("/events/{eventId}/users/{userId}")
    public ResponseEntity<Void> unregister(
            @PathVariable Long eventId,
            @PathVariable Long userId) {

        registrationService.unregisterUserFromEvent(userId, eventId);
        return ResponseEntity.noContent().build();
    }

    // 3. Obtenir toutes les inscriptions d'un événement (pour organisateur)
    @GetMapping("/events/{eventId}")
    public ResponseEntity<List<EventRegistration>> getEventRegistrations(
            @PathVariable Long eventId) {

        List<EventRegistration> registrations = registrationService.getEventRegistrations(eventId);
        return ResponseEntity.ok(registrations);
    }

    // 4. Obtenir les inscriptions d'un utilisateur
    @GetMapping("/users/{userId}")
    public ResponseEntity<List<EventRegistration>> getUserRegistrations(
            @PathVariable Long userId) {

        List<EventRegistration> registrations = registrationService.getUserRegistrations(userId);
        return ResponseEntity.ok(registrations);
    }

    // 5. Compter les inscriptions d'un événement
    @GetMapping("/events/{eventId}/count")
    public ResponseEntity<Long> countEventRegistrations(
            @PathVariable Long eventId) {

        Long count = registrationService.countEventRegistrations(eventId);
        return ResponseEntity.ok(count);
    }

    // 6. Vérifier si un utilisateur est inscrit
    @GetMapping("/events/{eventId}/users/{userId}/status")
    public ResponseEntity<Boolean> isUserRegistered(
            @PathVariable Long eventId,
            @PathVariable Long userId) {

        boolean isRegistered = registrationService.isUserRegistered(userId, eventId);
        return ResponseEntity.ok(isRegistered);
    }


}
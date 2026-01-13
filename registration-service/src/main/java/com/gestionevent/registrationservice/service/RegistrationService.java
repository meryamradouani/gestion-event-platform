package com.gestionevent.registrationservice.service;

import com.gestionevent.registrationservice.model.EventRegistration;
import com.gestionevent.registrationservice.repository.EventRegistrationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationService {

    private final EventRegistrationRepository repository;
    private final RegistrationEventPublisher eventPublisher;

    // S'inscrire à un événement
    @Transactional
    public EventRegistration registerUserToEvent(Long userId, Long eventId, Integer maxParticipants) {
        log.info("Starting registration for userId: {} to eventId: {}", userId, eventId);
        
        if (repository.existsByUserIdAndEventId(userId, eventId)) {
            log.warn("Registration failed: User {} already registered to event {}", userId, eventId);
            throw new RuntimeException("Vous êtes déjà inscrit à cet événement.");
        }

        if (maxParticipants != null) {
            long currentCount = repository.countByEventId(eventId);
            if (currentCount >= maxParticipants) {
                log.warn("Registration failed: Event {} is full ({} participants)", eventId, currentCount);
                throw new RuntimeException("Cet événement est complet.");
            }
        }

        EventRegistration registration = new EventRegistration(userId, eventId);
        EventRegistration saved = repository.save(registration);
        log.info("Registration saved in DB with ID: {}", saved.getId());

        // Envoi asynchrone (via try-catch interne de publisher)
        try {
            String eventTitle = "Événement #" + eventId;
            log.info("Attempting to publish Kafka events for userId: {}", userId);
            eventPublisher.publishRegistrationCreated(userId, eventId, eventTitle);
            log.info("Kafka publish triggered successfully");
        } catch (Exception e) {
            log.error("Failed to trigger Kafka publish, but registration is saved", e);
        }

        return saved;
    }

    @Transactional
    public void unregisterUserFromEvent(Long userId, Long eventId) {
        log.info("Attempting robust deletion for userId: {} and eventId: {}", userId, eventId);
        repository.deleteByUserIdAndEventId(userId, eventId);
        log.info("Atomic delete operation completed for userId: {} and eventId: {}", userId, eventId);
    }

    // Obtenir toutes les inscriptions d'un événement (pour organisateur)
    public List<EventRegistration> getEventRegistrations(Long eventId) {
        return repository.findByEventId(eventId);
    }

    // Obtenir les inscriptions d'un utilisateur
    public List<EventRegistration> getUserRegistrations(Long userId) {
        return repository.findByUserId(userId);
    }

    // Compter les inscriptions d'un événement
    public Long countEventRegistrations(Long eventId) {
        return repository.countByEventId(eventId);
    }

    // Vérifier si un utilisateur est inscrit
    public boolean isUserRegistered(Long userId, Long eventId) {
        return repository.existsByUserIdAndEventId(userId, eventId);
    }


}
package com.gestionevent.registrationservice.service;

import com.gestionevent.registrationservice.model.EventRegistration;
import com.gestionevent.registrationservice.repository.EventRegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final EventRegistrationRepository repository;
    private final RegistrationEventPublisher eventPublisher;

    // S'inscrire à un événement
    @Transactional
    public EventRegistration registerUserToEvent(Long userId, Long eventId, Integer maxParticipants) {
        // 1. Vérifier si déjà inscrit
        if (repository.existsByUserIdAndEventId(userId, eventId)) {
            throw new RuntimeException("Utilisateur déjà inscrit à cet événement");
        }

        // 2. Vérifier limite de participants
        Long currentCount = repository.countByEventId(eventId);
        if (maxParticipants != null && currentCount >= maxParticipants) {
            throw new RuntimeException("Événement complet");
        }

        // 3. Créer et sauvegarder l'inscription
        EventRegistration registration = new EventRegistration(userId, eventId);
        EventRegistration saved = repository.save(registration);

        // 4. PUBLIER LES DEUX ÉVÉNEMENTS KAFKA

        // Pour générer le titre de l'événement (vous pouvez améliorer ça)
        String eventTitle = "Événement #" + eventId;

        // 🔵 Pour P4 (Notification-Service) - Topic: registrations.created
        eventPublisher.publishRegistrationCreated(userId, eventId, eventTitle);

        LocalDateTime eventDate = LocalDateTime.now().plusDays(7); // Date par défaut


        eventPublisher.publishRegistrationConfirmed(userId, eventId, eventTitle, eventDate);

        return saved;
    }

    // Se désinscrire d'un événement
    @Transactional
    public void unregisterUserFromEvent(Long userId, Long eventId) {
        EventRegistration registration = repository.findByUserIdAndEventId(userId, eventId)
                .orElseThrow(() -> new RuntimeException("Inscription non trouvée"));

        repository.delete(registration);
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
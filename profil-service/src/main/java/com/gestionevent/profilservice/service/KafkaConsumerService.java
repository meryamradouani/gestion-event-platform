package com.gestionevent.profilservice.service;

import com.gestionevent.profilservice.dto.*;
import com.gestionevent.profilservice.entity.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import com.gestionevent.profilservice.dto.UserAuthenticatedEvent;
import com.gestionevent.profilservice.dto.RegistrationConfirmedEvent;
import com.gestionevent.profilservice.dto.EventCreatedEvent;

@Service
public class KafkaConsumerService {

    private final ProfileService profileService;

    public KafkaConsumerService(ProfileService profileService) {
        this.profileService = profileService;
    }

    // 1. Écouter la connexion d'un utilisateur
    @KafkaListener(topics = "user.authenticated", groupId = "profile-group")
    public void handleUserAuthenticated(UserAuthenticatedEvent event) {
        System.out.println("🎯 [Kafka] User authenticated: " + event.getUserId() + " (" + event.getEmail() + ")");

        // Crée ou met à jour le profil (Last Login + Full Name)
        profileService.createOrUpdateProfileAfterLogin(
                event.getUserId(),
                event.getEmail(),
                event.getFullName());
    }

    // 2. Écouter l'inscription à un événement
    @KafkaListener(topics = "registration.confirmed", groupId = "profile-group")
    public void handleRegistrationConfirmed(RegistrationConfirmedEvent event) {
        System.out.println("🎯 [Kafka] Registration confirmed - User: " +
                event.getUserId() + " to Event: " + event.getEventId());

        // Ajoute à l'historique de l'étudiant
        profileService.addEventToHistory(
                event.getUserId(),
                event.getEventId(),
                "inscrit" // Type d'historique
        );
    }

    // 3. Écouter la création d'un événement
    @KafkaListener(topics = "event.created", groupId = "profile-group")
    public void handleEventCreated(EventCreatedEvent event) {
        System.out.println("🎯 [Kafka] Event created by organizer: " +
                event.getOrganizerId() + " - Event: " + event.getEventTitle());

        // Ajoute à l'historique de l'organisateur
        profileService.addEventToHistory(
                event.getOrganizerId(),
                event.getEventId(),
                "créé" // Type d'historique
        );
    }
}

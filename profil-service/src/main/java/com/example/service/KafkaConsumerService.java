package com.example.service;

import com.example.dto.UserAuthenticatedEvent;
import com.example.dto.RegistrationConfirmedEvent;
import com.example.dto.EventCreatedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    private final ProfileService profileService;

    public KafkaConsumerService(ProfileService profileService) {
        this.profileService = profileService;
    }

    @KafkaListener(topics = "user.authenticated")
    public void handleUserAuthenticated(UserAuthenticatedEvent event) {
        System.out.println("🎯 [Kafka] User authenticated: " + event.getUserId() + " (" + event.getEmail() + ")");

        profileService.createOrUpdateProfileAfterLogin(
                event.getUserId(),
                event.getEmail(),
                event.getFullName(),
                event.getRole(),
                event.getInstitution(),
                event.getMajor(),
                event.getOrganizationName(),
                event.getOrganizationType()
        );
    }

    @KafkaListener(topics = "registration.confirmed")
    public void handleRegistrationConfirmed(RegistrationConfirmedEvent event) {
        System.out.println("🎯 [Kafka] Registration confirmed - User: " +
                event.getUserId() + " to Event: " + event.getEventId());

        profileService.addEventToHistory(
                event.getUserId(),
                event.getEventId(),
                "inscrit"
        );
    }

    @KafkaListener(topics = "event.created")
    public void handleEventCreated(EventCreatedEvent event) {
        System.out.println("🎯 [Kafka] Event created by organizer: " +
                event.getOrganizerId() + " - Event: " + event.getEventTitle());

        profileService.addEventToHistory(
                event.getOrganizerId(),
                event.getEventId(),
                "créé"
        );
    }
}
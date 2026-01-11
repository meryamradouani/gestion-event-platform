package com.example.service;

// src/main/java/com/events/profile/service/KafkaTest.java

import com.example.dto.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class KafkaTest {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaTest(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendAllTestEvents() {
        System.out.println("🚀 Sending test events to Kafka...");

        // 1. Test user.authenticated
        sendUserAuthenticated();

        // 2. Test event.created
        sendEventCreated();

        // 3. Test registration.confirmed
        sendRegistrationConfirmed();

        System.out.println("✅ All test events sent!");
    }

    private void sendUserAuthenticated() {
        UserAuthenticatedEvent event = new UserAuthenticatedEvent(
                10L, // userId
                "Kenza Mouktabil", // full name
                "kenza@gmail.com", // email
                "student", // role
                LocalDateTime.now() // loginTime
        );
        kafkaTemplate.send("user.authenticated", event);
        System.out.println("📤 Sent user.authenticated for userId: 1");
    }

    private void sendEventCreated() {
        EventCreatedEvent event = new EventCreatedEvent(
                101L, // eventId
                2L, // organizerId (user qui crée l'événement)
                "Conférence IA", // eventTitle
                "conférence", // eventType
                LocalDateTime.now().plusDays(7), // eventDate (dans 7 jours)
                "Amphi A101", // location
                LocalDateTime.now() // creationTime
        );
        kafkaTemplate.send("event.created", event);
        System.out.println("📤 Sent event.created by organizerId: 2");
    }

    private void sendRegistrationConfirmed() {
        RegistrationConfirmedEvent event = new RegistrationConfirmedEvent(
                1L, // userId (étudiant qui s'inscrit)
                101L, // eventId (même que ci-dessus)
                "Conférence IA", // eventTitle
                LocalDateTime.now().plusDays(7), // eventDate
                LocalDateTime.now() // registrationTime
        );
        kafkaTemplate.send("registration.confirmed", event);
        System.out.println("📤 Sent registration.confirmed: user 1 → event 101");
    }
}

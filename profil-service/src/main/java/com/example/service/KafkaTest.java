package com.example.service;

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
        sendUserAuthenticated();
        sendOrganizerAuthenticated();
        System.out.println("✅ All test events sent!");
    }

    private void sendUserAuthenticated() {
        UserAuthenticatedEvent event = new UserAuthenticatedEvent(
                70L,
                null, // Pas d'email
                "Meryam Radouani",
                "student",
                LocalDateTime.now(),
                "ENSA",
                "Informatique",
                null,
                null
        );
        kafkaTemplate.send("user.authenticated", event);
        System.out.println("📤 Sent user.authenticated (Student) for userId: 70");
    }

    private void sendOrganizerAuthenticated() {
        UserAuthenticatedEvent event = new UserAuthenticatedEvent(
                80L,
                null, // Pas d'email
                "Directeur Robotique",
                "organizer",
                LocalDateTime.now(),
                null,
                null,
                "Club Robotique",
                "Association"
        );
        kafkaTemplate.send("user.authenticated", event);
        System.out.println("📤 Sent user.authenticated (Organizer) for userId: 80");
    }
}

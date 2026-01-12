package com.gestionevent.registrationservice.service;

import com.gestionevent.registrationservice.event.RegistrationCreatedEvent;
import com.gestionevent.registrationservice.event.RegistrationConfirmedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    // Topic pour P4 (Notification-Service)
    private static final String TOPIC_REGISTRATION_CREATED = "registrations.created";

    // Topic pour P5 (Profile-Service)
    private static final String TOPIC_REGISTRATION_CONFIRMED = "registration.confirmed";

    // Méthode pour P4
    public void publishRegistrationCreated(Long userId, Long eventId, String eventTitle) {
        try {
            RegistrationCreatedEvent event = new RegistrationCreatedEvent(
                    userId,
                    eventId,
                    eventTitle,
                    LocalDateTime.now()
            );

            kafkaTemplate.send(TOPIC_REGISTRATION_CREATED, event);
            log.info("📤 Message Kafka envoyé au topic '{}' : {}", TOPIC_REGISTRATION_CREATED, event);

        } catch (Exception e) {
            log.error("❌ Erreur lors de l'envoi du message Kafka", e);
        }
    }

    // NOUVELLE MÉTHODE pour P5
    public void publishRegistrationConfirmed(Long userId, Long eventId, String eventTitle, LocalDateTime eventDate) {
        try {
            RegistrationConfirmedEvent event = new RegistrationConfirmedEvent(
                    userId,
                    eventId,
                    eventTitle,
                    eventDate,
                    LocalDateTime.now()
            );

            kafkaTemplate.send(TOPIC_REGISTRATION_CONFIRMED, event);
            log.info("📤 Message Kafka envoyé au topic '{}' : {}", TOPIC_REGISTRATION_CONFIRMED, event);
            System.out.println("✅ Registration confirmed event sent for userId: " + userId);

        } catch (Exception e) {
            log.error("❌ Erreur lors de l'envoi du message registration.confirmed", e);
        }
    }
}
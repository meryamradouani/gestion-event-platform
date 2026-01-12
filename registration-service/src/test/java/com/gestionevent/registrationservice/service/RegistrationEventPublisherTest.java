package com.gestionevent.registrationservice.service;

import com.gestionevent.registrationservice.event.RegistrationConfirmedEvent;
import com.gestionevent.registrationservice.event.RegistrationCreatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class RegistrationEventPublisherTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private RegistrationEventPublisher registrationEventPublisher;

    private Long userId = 1L;
    private Long eventId = 100L;
    private String eventTitle = "Test Event";

    @Test
    void publishRegistrationCreated_shouldSendKafkaMessage() {
        // Act
        registrationEventPublisher.publishRegistrationCreated(userId, eventId, eventTitle);

        // Assert
        verify(kafkaTemplate).send(eq("registrations.created"), any(RegistrationCreatedEvent.class));
    }

    @Test
    void publishRegistrationConfirmed_shouldSendKafkaMessage() {
        // Arrange
        LocalDateTime eventDate = LocalDateTime.now().plusDays(1);

        // Act
        registrationEventPublisher.publishRegistrationConfirmed(userId, eventId, eventTitle, eventDate);

        // Assert
        verify(kafkaTemplate).send(eq("registration.confirmed"), any(RegistrationConfirmedEvent.class));
    }
}

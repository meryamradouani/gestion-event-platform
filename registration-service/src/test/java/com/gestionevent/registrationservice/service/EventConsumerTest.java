package com.gestionevent.registrationservice.service;

import com.gestionevent.registrationservice.event.EventCreatedMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
public class EventConsumerTest {

    @InjectMocks
    private EventConsumer eventConsumer;

    @Test
    void consumeEventCreated_shouldProcessMessageWithoutError() {
        // Arrange
        EventCreatedMessage message = EventCreatedMessage.builder()
                .eventId(1L)
                .organizerId(100L)
                .eventTitle("Test Kafka Event")
                .eventDescription("Description")
                .location("Paris")
                .eventDate(LocalDateTime.now())
                .creationTime(LocalDateTime.now())
                .build();

        // Act & Assert
        // Puisque la méthode ne fait que logger, on vérifie simplement qu'elle ne plante pas
        assertDoesNotThrow(() -> eventConsumer.consumeEventCreated(message));
    }
}

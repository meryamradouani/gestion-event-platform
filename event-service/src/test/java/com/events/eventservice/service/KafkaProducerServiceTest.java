package com.events.eventservice.service;

import com.events.eventservice.dto.EventCreatedMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class KafkaProducerServiceTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private KafkaProducerService kafkaProducerService;

    private EventCreatedMessage eventCreatedMessage;

    @BeforeEach
    void setUp() {
        // Since @Value is not injected by Mockito, we set it manually
        ReflectionTestUtils.setField(kafkaProducerService, "eventCreatedTopic", "events.created");

        eventCreatedMessage = EventCreatedMessage.builder()
                .eventId(1L)
                .organizerId(100L)
                .eventTitle("Test Event")
                .eventDescription("Description")
                .eventDate(LocalDateTime.now().plusDays(1))
                .location("Paris")
                .creationTime(LocalDateTime.now())
                .build();
    }

    @Test
    void sendEventCreated_shouldSendKafkaMessage() {
        // Act
        kafkaProducerService.sendEventCreated(eventCreatedMessage);

        // Assert
        verify(kafkaTemplate).send(eq("events.created"), eq(eventCreatedMessage));
    }
}

package com.gestionevent.backend.service;

import com.gestionevent.backend.event.EventCreatedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class KafkaConsumerServiceTest {

    @Mock
    private FirebaseNotificationService firebaseService;

    @InjectMocks
    private KafkaConsumerService kafkaConsumerService;

    @Test
    public void testConsumeEventCreated_CallsProcessNewEvent() {
        // GIVEN: Un événement simulé (Mock Event)
        EventCreatedEvent event = new EventCreatedEvent(
            999L, 
            "Event de Test Mock", 
            "Description de test", 
            1L, 
            LocalDateTime.now()
        );

        // WHEN: On simule la réception par le listener
        kafkaConsumerService.consumeEventCreated(event);

        // THEN: On vérifie que le service de traitement a bien été appelé
        verify(firebaseService, times(1)).processNewEvent(any(EventCreatedEvent.class));
    }
}

package com.gestionevent.eventservice.service;

import com.gestionevent.eventservice.dto.EventCreatedMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.event-created}")
    private String eventCreatedTopic; // UN SEUL topic

    public KafkaProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEventCreated(EventCreatedMessage message) {
        try {
            kafkaTemplate.send(eventCreatedTopic, message);
            log.info("Message envoyé vers topic {} - Événement ID: {}",
                    eventCreatedTopic, message.getEventId());
        } catch (Exception e) {
            log.error("Erreur Kafka pour l'événement {}", message.getEventId(), e);
        }
    }
}

package com.gestionevent.registrationservice.service;

import com.gestionevent.registrationservice.event.EventCreatedMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EventConsumer {

    @KafkaListener(topics = "events.created", groupId = "registration-service-group")
    public void consumeEventCreated(EventCreatedMessage message) {
        log.info("📥 Réception d'un nouvel événement via Kafka : {}", message);
        
        // Logique métier ici : par exemple, stocker une copie locale de l'événement si nécessaire
        // Pour l'instant, on logue juste les détails
        log.info("Détails de l'événement reçu : ID={}, Titre={}, Date={}", 
                message.getEventId(), message.getEventTitle(), message.getEventDate());
    }
}

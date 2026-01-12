package com.gestionevent.backend.controller;

import com.gestionevent.backend.event.EventCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;


@RestController
@RequestMapping("/api/test")
public class KafkaTestController {
    
    @Autowired
    private KafkaTemplate<String, EventCreatedEvent> kafkaTemplate;
    
    @PostMapping("/send-kafka-event")
public String sendKafkaEvent() {
    EventCreatedEvent event = new EventCreatedEvent(
        1001L,  // eventId
        "Test Kafka Insertion",  // eventTitle
        "Cet événement a été créé via Kafka",  // eventDescription
        1L,  // creatorId ⬅️ D'ABORD
        LocalDateTime.now().plusDays(1)  // eventDate ⬅️ PUIS
    );
    
    kafkaTemplate.send("events.created", event);
    return "✅ Message Kafka envoyé !";
}
}
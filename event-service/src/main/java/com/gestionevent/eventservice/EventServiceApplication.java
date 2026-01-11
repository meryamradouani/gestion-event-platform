package com.gestionevent.eventservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class EventServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventServiceApplication.class, args);
    }

    @GetMapping("/")
    public String home() {
        return "Event-Service est en marche! Utilisez /api/events pour accéder aux événements.";
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}

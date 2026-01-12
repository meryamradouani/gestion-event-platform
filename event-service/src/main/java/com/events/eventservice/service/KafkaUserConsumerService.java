package com.events.eventservice.service;

import com.events.eventservice.dto.UserAuthenticatedEvent;
import com.events.eventservice.model.UserRole;
import com.events.eventservice.repository.UserRoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaUserConsumerService {

    private final UserRoleRepository userRoleRepository;

    public KafkaUserConsumerService(UserRoleRepository userRoleRepository) {
        this.userRoleRepository = userRoleRepository;
    }

    @KafkaListener(topics = "user.authenticated", groupId = "event-service-group")
    public void handleUserAuthenticated(UserAuthenticatedEvent event) {
        log.info("Mise à jour du rôle pour l'utilisateur ID: {} - Rôle: {}", event.getUserId(), event.getRole());

        UserRole userRole = new UserRole(event.getUserId(), event.getRole());
        userRoleRepository.save(userRole);
    }
}

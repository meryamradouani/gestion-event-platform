package com.events.eventservice.service;

import com.events.eventservice.dto.UserAuthenticatedEvent;
import com.events.eventservice.model.UserRole;
import com.events.eventservice.repository.UserRoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class KafkaUserConsumerServiceTest {

    @Mock
    private UserRoleRepository userRoleRepository;

    @InjectMocks
    private KafkaUserConsumerService kafkaUserConsumerService;

    @Test
    void handleUserAuthenticated_shouldSaveUserRole() {
        // Arrange
        UserAuthenticatedEvent event = UserAuthenticatedEvent.builder()
                .userId(1L)
                .email("test@example.com")
                .fullName("Test User")
                .role("organizer")
                .loginTime(LocalDateTime.now())
                .build();

        // Act
        kafkaUserConsumerService.handleUserAuthenticated(event);

        // Assert
        verify(userRoleRepository).save(any(UserRole.class));
    }
}

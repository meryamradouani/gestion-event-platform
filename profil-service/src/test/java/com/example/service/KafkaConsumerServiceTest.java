package com.example.service;

import com.example.dto.EventCreatedEvent;
import com.example.dto.RegistrationConfirmedEvent;
import com.example.dto.UserAuthenticatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KafkaConsumerServiceTest {

    @Mock
    private ProfileService profileService;

    @InjectMocks
    private KafkaConsumerService kafkaConsumerService;

    private EventCreatedEvent eventCreatedEvent;
    private UserAuthenticatedEvent userAuthenticatedEvent;
    private RegistrationConfirmedEvent registrationConfirmedEvent;

    @BeforeEach
    void setUp() {
        // EventCreatedEvent setup
        eventCreatedEvent = new EventCreatedEvent(
                1L,
                100L,
                "Test Event",
                "Description",
                LocalDateTime.now().plusDays(1),
                "Paris",
                LocalDateTime.now());

        // UserAuthenticatedEvent setup
        userAuthenticatedEvent = new UserAuthenticatedEvent(
                100L,
                "test@example.com",
                "John Doe",
                "student",
                LocalDateTime.now());

        // RegistrationConfirmedEvent setup
        registrationConfirmedEvent = new RegistrationConfirmedEvent(
                100L,
                1L,
                "Test Event",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now());
    }

    @Test
    void handleEventCreated_shouldCallProfileService() {
        // Act
        kafkaConsumerService.handleEventCreated(eventCreatedEvent);

        // Assert
        verify(profileService, times(1)).addEventToHistory(
                eq(100L),
                eq(1L),
                eq("créé"));
    }

    @Test
    void handleUserAuthenticated_shouldCallProfileService() {
        // Act
        kafkaConsumerService.handleUserAuthenticated(userAuthenticatedEvent);

        // Assert
        verify(profileService, times(1)).createOrUpdateProfileAfterLogin(
                eq(100L),
                eq("test@example.com"),
                eq("John Doe"));
    }

    @Test
    void handleRegistrationConfirmed_shouldCallProfileService() {
        // Act
        kafkaConsumerService.handleRegistrationConfirmed(registrationConfirmedEvent);

        // Assert
        verify(profileService, times(1)).addEventToHistory(
                eq(100L),
                eq(1L),
                eq("inscrit"));
    }
}

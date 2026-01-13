package com.events.eventservice.service;

import com.events.eventservice.dto.CreateEventRequest;
import com.events.eventservice.dto.EventResponse;
import com.events.eventservice.model.Event;
import com.events.eventservice.model.EventStatus;
import com.events.eventservice.model.UserRole;
import com.events.eventservice.repository.EventImageRepository;
import com.events.eventservice.repository.EventRepository;
import com.events.eventservice.repository.UserRoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventImageRepository eventImageRepository;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @Mock
    private UserRoleRepository userRoleRepository;

    @InjectMocks
    private EventService eventService;

    private CreateEventRequest createEventRequest;
    private Event event;
    private UserRole organizerRole;
    private UserRole studentRole;

    @BeforeEach
    void setUp() {
        // Fix for potential NPE if @Value is not injected
        ReflectionTestUtils.setField(eventService, "defaultEventImage", "http://placeholder.com");
        ReflectionTestUtils.setField(eventService, "maxFileSize", 5242880L);

        createEventRequest = new CreateEventRequest();
        createEventRequest.setTitle("New Event");
        createEventRequest.setDescription("Description");
        createEventRequest.setEventDate(LocalDateTime.now().plusDays(10).toString());
        createEventRequest.setLocation("Paris");
        createEventRequest.setMaxParticipants(100);

        event = Event.builder()
                .id(1L)
                .title("New Event")
                .description("Description")
                .eventDate(LocalDateTime.now().plusDays(10))
                .location("Paris")
                .maxParticipants(100)
                .createdBy(100L)
                .status(EventStatus.ACTIVE)
                .build();

        organizerRole = new UserRole(100L, "organizer");
        studentRole = new UserRole(101L, "student");
    }

    @Test
    void createEvent_shouldSucceed_whenUserIsOrganizer() {
        // Arrange
        when(userRoleRepository.findById(100L)).thenReturn(Optional.of(organizerRole));
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        // Act
        EventResponse response = eventService.createEvent(createEventRequest, 100L);

        // Assert
        assertNotNull(response);
        assertEquals("New Event", response.getTitle());
        verify(kafkaProducerService).sendEventCreated(any());
    }

    @Test
    void createEvent_shouldThrowException_whenUserIsNotOrganizer() {
        // Arrange
        when(userRoleRepository.findById(101L)).thenReturn(Optional.of(studentRole));

        // Act & Assert
        assertThrows(SecurityException.class, () -> eventService.createEvent(createEventRequest, 101L));
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void createEvent_shouldThrowException_whenUserRoleUnknown() {
        // Arrange
        when(userRoleRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(SecurityException.class, () -> eventService.createEvent(createEventRequest, 999L));
    }
}

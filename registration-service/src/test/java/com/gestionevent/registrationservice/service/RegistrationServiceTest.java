package com.gestionevent.registrationservice.service;

import com.gestionevent.registrationservice.model.EventRegistration;
import com.gestionevent.registrationservice.repository.EventRegistrationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

    @Mock
    private EventRegistrationRepository repository;

    @InjectMocks
    private RegistrationService registrationService;

    @Test
    void testRegisterUserToEvent_Success() {
        // Arrange
        Long userId = 1L;
        Long eventId = 1L;

        when(repository.existsByUserIdAndEventId(userId, eventId)).thenReturn(false);
        when(repository.countByEventId(eventId)).thenReturn(0L);
        when(repository.save(any(EventRegistration.class))).thenAnswer(invocation -> {
            EventRegistration reg = invocation.getArgument(0);
            reg.setId(1L);  // Simuler l'ID généré
            return reg;
        });

        // Act
        EventRegistration result = registrationService.registerUserToEvent(userId, eventId, 10);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(eventId, result.getEventId());
        assertEquals("pending", result.getAttendanceStatus());
        verify(repository, times(1)).save(any(EventRegistration.class));
    }

    @Test
    void testRegisterUserToEvent_UserAlreadyRegistered() {
        // Arrange
        Long userId = 1L;
        Long eventId = 1L;

        when(repository.existsByUserIdAndEventId(userId, eventId)).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> registrationService.registerUserToEvent(userId, eventId, null));

        assertEquals("Utilisateur déjà inscrit à cet événement", exception.getMessage());
    }
}
package com.events.eventservice.service;

import com.events.eventservice.dto.CreateEventRequest;
import com.events.eventservice.dto.EventResponse;
import com.events.eventservice.model.Event;
import com.events.eventservice.model.EventStatus;
import com.events.eventservice.model.UserRole;
import com.events.eventservice.repository.EventRepository;
import com.events.eventservice.repository.UserRoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests d'intégration pour EventService.
 * Utilise la base de données H2 (en mémoire) au lieu de Mockito.
 * Valide les opérations CRUD réelles.
 */
@SpringBootTest
@Transactional // Annule les modifications DB après chaque test
public class EventServiceIntegrationTest {

    @Autowired
    private EventService eventService;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    private Long organizerId = 1L;
    private Long studentId = 2L;

    @BeforeEach
    void setUp() {
        // Préparer la base de données avec les rôles nécessaires
        userRoleRepository.save(new UserRole(organizerId, "organizer"));
        userRoleRepository.save(new UserRole(studentId, "student"));
    }

    @Test
    void testCreateEvent_Success() {
        // 1. Préparer la requête
        CreateEventRequest request = new CreateEventRequest();
        request.setTitle("Integration Test Event");
        request.setDescription("Une description réelle");
        request.setEventDate(LocalDateTime.now().plusDays(5).toString());
        request.setLocation("Campus");
        request.setMaxParticipants(50);

        // 2. Exécuter l'action (Création par un organisateur)
        EventResponse response = eventService.createEvent(request, organizerId);

        // 3. Vérifications (Assertions JUnit)
        assertNotNull(response.getId(), "L'ID ne doit pas être null après sauvegarde");
        assertEquals("Integration Test Event", response.getTitle());
        assertEquals(EventStatus.ACTIVE, response.getStatus());

        // Vérifier que c'est bien en base de données
        assertTrue(eventRepository.findById(response.getId()).isPresent());
    }

    @Test
    void testCreateEvent_Security_Forbidden() {
        // 1. Préparer la requête
        CreateEventRequest request = new CreateEventRequest();
        request.setTitle("Hacked Event");
        request.setDescription("Should not exist");
        request.setEventDate(LocalDateTime.now().plusDays(5).toString());
        request.setLocation("Nowhere");

        // 2. Exécuter & Vérifier l'exception (Sécurité)
        assertThrows(SecurityException.class, () -> {
            eventService.createEvent(request, studentId);
        });
    }

    @Test
    void testGetEvent_Success() {
        // 1. Créer une donnée directement en base (Setup)
        Event event = Event.builder()
                .title("Existing Event")
                .description("Desc")
                .eventDate(LocalDateTime.now().plusDays(2))
                .location("Room 101")
                .createdBy(organizerId)
                .status(EventStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        event = eventRepository.save(event);

        // 2. Appeler le service
        EventResponse response = eventService.getEventById(event.getId());

        // 3. Vérifier
        assertEquals("Existing Event", response.getTitle());
        assertEquals(organizerId, response.getCreatedBy());
    }

    @Test
    void testUpdateEvent_Success() {
        // 1. Setup
        Event event = eventRepository.save(Event.builder()
                .title("Old Title")
                .description("Desc")
                .eventDate(LocalDateTime.now().plusDays(10))
                .location("Old Loc")
                .createdBy(organizerId)
                .status(EventStatus.ACTIVE)
                .build());

        // 2. Action - Mise à jour
        Event updates = new Event();
        updates.setTitle("New Title");
        updates.setDescription("New Desc");
        updates.setEventDate(event.getEventDate());
        updates.setLocation("New Loc");
        updates.setMaxParticipants(100);
        updates.setStatus(EventStatus.ACTIVE);

        EventResponse response = eventService.updateEvent(event.getId(), updates, organizerId);

        // 3. Vérifier
        assertEquals("New Title", response.getTitle());

        // Vérifier en base
        Event dbEvent = eventRepository.findById(event.getId()).get();
        assertEquals("New Title", dbEvent.getTitle());
    }

    @Test
    void testDeleteEvent_Success() {
        // 1. Setup
        Event event = eventRepository.save(Event.builder()
                .title("To Delete")
                .createdBy(organizerId)
                .eventDate(LocalDateTime.now().plusDays(1))
                .location("Paris") // Ajout du lieu obligatoire
                .status(EventStatus.ACTIVE)
                .build());

        // 2. Action
        eventService.deleteEvent(event.getId(), organizerId);

        // 3. Vérifier que l'événement n'existe plus
        assertFalse(eventRepository.findById(event.getId()).isPresent());
    }
}

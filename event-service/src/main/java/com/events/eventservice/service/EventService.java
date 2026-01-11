package com.events.eventservice.service;

import com.events.eventservice.dto.CreateEventRequest;
import com.events.eventservice.dto.EventCreatedMessage;
import com.events.eventservice.dto.EventResponse;
import com.events.eventservice.model.Event;
import com.events.eventservice.model.EventImage;
import com.events.eventservice.repository.UserRoleRepository;
import com.events.eventservice.model.EventStatus;
import com.events.eventservice.repository.EventImageRepository;
import com.events.eventservice.repository.EventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventImageRepository eventImageRepository;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Value("${app.default-event-image:https://via.placeholder.com/600x400?text=Event+Image}")
    private String defaultEventImage;

    @Value("${file.max-size:5242880}") // 5MB par défaut
    private long maxFileSize;

    public List<EventResponse> getAllEvents() {
        log.info("Récupération de tous les événements");
        return eventRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public EventResponse getEventById(Long id) {
        log.info("Récupération de l'événement avec ID: {}", id);
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Événement non trouvé avec ID: " + id));
        return convertToResponse(event);
    }

    @Transactional
    public EventResponse createEvent(CreateEventRequest request, Long creatorId) {
        return createEventWithImage(request, null, creatorId);
    }

    @Transactional
    public EventResponse createEventWithImage(CreateEventRequest request, MultipartFile imageFile, Long creatorId) {
        // Vérifier le rôle
        checkOrganizerRole(creatorId);

        log.info("Création d'un nouvel événement avec image par l'utilisateur {}", creatorId);

        // Validation de la date
        validateEventDate(request.getEventDate());

        // Création de l'entité Event
        Event event = Event.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .eventDate(request.getEventDate())
                .location(request.getLocation())
                .maxParticipants(request.getMaxParticipants())
                .createdBy(creatorId)
                .status(EventStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Sauvegarde en base
        Event savedEvent = eventRepository.save(event);
        log.info("Événement créé avec ID: {}", savedEvent.getId());

        // Gestion de l'image (BLOB)
        handleEventImageUpload(imageFile, savedEvent);

        // Publier le message Kafka
        publishEventCreatedMessage(savedEvent);

        return convertToResponse(savedEvent);
    }

    @Transactional
    public EventResponse updateEvent(Long id, Event eventDetails, Long userId) {
        log.info("Mise à jour de l'événement avec ID: {}", id);

        // Vérifier le rôle
        checkOrganizerRole(userId);

        Event event = getEventEntityById(id);

        // Vérifier que l'utilisateur est le créateur
        if (!event.getCreatedBy().equals(userId)) {
            throw new SecurityException("Seul le créateur peut modifier l'événement");
        }

        // Validation de la date
        validateEventDate(eventDetails.getEventDate());

        // Mettre à jour les champs
        event.setTitle(eventDetails.getTitle());
        event.setDescription(eventDetails.getDescription());
        event.setEventDate(eventDetails.getEventDate());
        event.setLocation(eventDetails.getLocation());
        event.setMaxParticipants(eventDetails.getMaxParticipants());
        event.setStatus(eventDetails.getStatus());
        event.setUpdatedAt(LocalDateTime.now());

        updateEventStatusIfFull(event);

        Event updatedEvent = eventRepository.save(event);
        return convertToResponse(updatedEvent);
    }

    @Transactional
    public EventResponse updateEventWithImage(Long id, CreateEventRequest request, MultipartFile imageFile,
                                              Long userId) {
        log.info("Mise à jour complète de l'événement avec ID: {}", id);

        // Vérifier le rôle
        checkOrganizerRole(userId);

        Event event = getEventEntityById(id);

        // Vérifier que l'utilisateur est le créateur
        if (!event.getCreatedBy().equals(userId)) {
            throw new SecurityException("Seul le créateur peut modifier l'événement");
        }

        // Validation de la date
        validateEventDate(request.getEventDate());

        // Mettre à jour les champs
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setEventDate(request.getEventDate());
        event.setLocation(request.getLocation());
        event.setMaxParticipants(request.getMaxParticipants());
        event.setUpdatedAt(LocalDateTime.now());

        // Gestion de l'image
        if (imageFile != null && !imageFile.isEmpty()) {
            handleEventImageUpload(imageFile, event);
        }

        Event updatedEvent = eventRepository.save(event);
        return convertToResponse(updatedEvent);
    }

    @Transactional
    public EventResponse updateEventImage(Long id, MultipartFile imageFile, Long userId) throws IOException {
        log.info("Mise à jour de l'image de l'événement avec ID: {}", id);

        // Vérifier le rôle
        checkOrganizerRole(userId);

        Event event = getEventEntityById(id);

        // Vérifier que l'utilisateur est le créateur
        if (!event.getCreatedBy().equals(userId)) {
            throw new SecurityException("Seul le créateur peut modifier l'image de l'événement");
        }

        // Valider le fichier image
        validateImageFile(imageFile);

        // Supprimer l'ancienne image si elle existe
        deleteEventImage(id);

        // Sauvegarder la nouvelle image
        if (imageFile != null && !imageFile.isEmpty()) {
            EventImage eventImage = EventImage.builder()
                    .data(imageFile.getBytes())
                    .contentType(imageFile.getContentType())
                    .event(event)
                    .build();

            eventImageRepository.save(eventImage);
            log.info("Image mise à jour pour l'événement ID: {}", id);
        }

        event.setUpdatedAt(LocalDateTime.now());
        eventRepository.save(event);

        return convertToResponse(event);
    }

    @Transactional
    public EventResponse deleteEventImage(Long id, Long userId) {
        log.info("Suppression de l'image de l'événement avec ID: {}", id);

        // Vérifier le rôle
        checkOrganizerRole(userId);

        Event event = getEventEntityById(id);

        // Vérifier que l'utilisateur est le créateur
        if (!event.getCreatedBy().equals(userId)) {
            throw new SecurityException("Seul le créateur peut supprimer l'image de l'événement");
        }

        // Supprimer l'image
        deleteEventImage(id);

        event.setUpdatedAt(LocalDateTime.now());
        Event updatedEvent = eventRepository.save(event);

        return convertToResponse(updatedEvent);
    }

    private void deleteEventImage(Long eventId) {
        List<EventImage> images = eventImageRepository.findByEventId(eventId);
        if (!images.isEmpty()) {
            eventImageRepository.deleteAll(images);
            log.info("{} image(s) supprimée(s) pour l'événement ID: {}", images.size(), eventId);
        }
    }

    @Transactional
    public void deleteEvent(Long id, Long userId) {
        log.info("Suppression de l'événement avec ID: {}", id);

        // Vérifier le rôle
        checkOrganizerRole(userId);

        Event event = getEventEntityById(id);

        // Vérifier que l'utilisateur est le créateur
        if (!event.getCreatedBy().equals(userId)) {
            throw new SecurityException("Seul le créateur peut supprimer l'événement");
        }

        // Supprimer les images associées
        deleteEventImage(id);

        // Supprimer l'événement
        eventRepository.delete(event);
        log.info("Événement ID: {} supprimé avec succès", id);
    }

    // Vérification du rôle organisateur
    private void checkOrganizerRole(Long userId) {
        userRoleRepository.findById(userId).ifPresentOrElse(
                userRole -> {
                    if (!"organizer".equalsIgnoreCase(userRole.getRole())) {
                        throw new SecurityException("Seuls les organisateurs peuvent effectuer cette action.");
                    }
                },
                () -> {
                    // Optionnel : Si l'utilisateur n'est pas trouvé (pas encore login ou erreur
                    // sync),
                    // on peut soit bloquer, soit laisser passer (politique par défaut).
                    // Ici, on bloque par sécurité.
                    throw new SecurityException("Utilisateur non identifié ou rôle inconnu.");
                });
    }

    public java.util.Optional<EventImage> getEventImage(Long eventId) {
        return eventImageRepository.findByEventId(eventId).stream().findFirst();
    }

    public List<EventResponse> getUpcomingEvents() {
        log.info("Récupération des événements à venir");
        return eventRepository.findByEventDateAfter(LocalDateTime.now()).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<EventResponse> getEventsByOrganizer(Long organizerId) {
        log.info("Récupération des événements de l'organisateur: {}", organizerId);
        return eventRepository.findByCreatedBy(organizerId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<EventResponse> getActiveEvents() {
        log.info("Récupération des événements actifs");
        return eventRepository.findAll().stream()
                .filter(e -> e.getStatus() == EventStatus.ACTIVE)
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<EventResponse> getEventsByStatus(EventStatus status) {
        log.info("Récupération des événements avec statut: {}", status);
        return eventRepository.findAll().stream()
                .filter(e -> e.getStatus() == status)
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private void handleEventImageUpload(MultipartFile imageFile, Event event) {
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                // Valider le fichier image
                validateImageFile(imageFile);

                // Supprimer les anciennes images si elles existent
                deleteEventImage(event.getId());

                // Sauvegarder la nouvelle image
                EventImage eventImage = EventImage.builder()
                        .data(imageFile.getBytes())
                        .contentType(imageFile.getContentType())
                        .event(event)
                        .build();

                eventImageRepository.save(eventImage);
                log.info("Image sauvegardée pour l'événement ID: {}", event.getId());

            } catch (IOException e) {
                log.error("Erreur lors de la lecture de l'image pour l'événement ID: {}", event.getId(), e);
                throw new RuntimeException("Erreur lors du traitement de l'image", e);
            }
        }
    }

    private void validateEventDate(LocalDateTime eventDate) {
        if (eventDate.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La date de l'événement doit être dans le futur");
        }
    }

    private void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return;
        }

        // Vérifier si c'est une image
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Le fichier doit être une image");
        }

        // Vérifier la taille du fichier
        if (file.getSize() > maxFileSize) {
            throw new IllegalArgumentException("La taille de l'image ne doit pas dépasser "
                    + (maxFileSize / 1024 / 1024) + "MB");
        }

        // Vérifier les types MIME autorisés
        if (!isAllowedContentType(contentType)) {
            throw new IllegalArgumentException("Type de fichier non autorisé. Utilisez JPEG, PNG, GIF ou WEBP");
        }
    }

    private boolean isAllowedContentType(String contentType) {
        return contentType != null && (contentType.equals("image/jpeg") ||
                contentType.equals("image/png") ||
                contentType.equals("image/gif") ||
                contentType.equals("image/webp") ||
                contentType.equals("image/jpg"));
    }

    private void updateEventStatusIfFull(Event event) {
        // À compléter avec la logique d'inscription
        // Pour l'instant, laisser vide
    }

    private void publishEventCreatedMessage(Event event) {
        try {
            EventCreatedMessage message = EventCreatedMessage.builder()
                    .eventId(event.getId())
                    .organizerId(event.getCreatedBy())
                    .eventTitle(event.getTitle())
                    .eventDescription(event.getDescription())
                    .eventDate(event.getEventDate())
                    .location(event.getLocation())
                    .creationTime(LocalDateTime.now())
                    .build();

            kafkaProducerService.sendEventCreated(message);
            log.info("Message Kafka publié pour l'événement ID: {}", event.getId());

        } catch (Exception e) {
            log.error("Erreur lors de la publication du message Kafka pour l'événement ID: {}",
                    event.getId(), e);
            // Ne pas bloquer l'opération principale si Kafka échoue
        }
    }

    // Méthode utilitaire pour obtenir l'entité Event
    private Event getEventEntityById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Événement non trouvé avec ID: " + id));
    }

    private EventResponse convertToResponse(Event event) {
        // Vérifier si l'événement a une image
        boolean hasImage = eventImageRepository.findByEventId(event.getId()).stream()
                .findFirst().isPresent();

        // Construire l'URL de l'image si elle existe
        String imageUrl = hasImage ? "/api/events/" + event.getId() + "/image" : null;

        return EventResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .imageUrl(imageUrl) // ← AJOUTEZ CE CHAMP
                .eventDate(event.getEventDate())
                .location(event.getLocation())
                .maxParticipants(event.getMaxParticipants())
                .createdBy(event.getCreatedBy())
                .status(event.getStatus())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .build();
    }
}
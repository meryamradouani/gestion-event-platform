package com.gestionevent.backend.service;

import com.gestionevent.backend.event.EventCreatedEvent;
import com.gestionevent.backend.model.Event;
import com.gestionevent.backend.repository.EventRepository;
import com.gestionevent.backend.util.NotificationConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EventService {

    private static final Logger log = LoggerFactory.getLogger(EventService.class);

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private FirebaseNotificationService firebaseNotificationService;

    @Autowired
    private TokenManagementService tokenManagementService;

    /**
     * Traite un événement créé depuis Kafka
     * 1. Enregistre l'événement en DB
     * 2. Envoie une notification push à tous les utilisateurs
     */
    @Transactional
    public void processEventCreated(EventCreatedEvent eventData) {
        log.info(NotificationConstants.SEPARATOR);
        log.info("📥 [EVENTS] Traitement événement créé: eventId={}, title='{}'",
                eventData.getEventId(), eventData.getEventTitle());

        try {
            // ÉTAPE 1 : Enregistrer l'événement en DB
            log.info("📝 [ÉTAPE 1] Enregistrement de l'événement en DB...");

            Event event = new Event();
            event.setId(eventData.getEventId());
            event.setTitle(eventData.getEventTitle());
            event.setDescription(eventData.getEventDescription());
            event.setEventDate(eventData.getEventDate());
            event.setCreatedBy(eventData.getCreatorId());
            event.setStatus("actif");

            event = eventRepository.save(event);
            log.info("✅ [ÉTAPE 1] Événement enregistré en DB avec ID: {}", event.getId());

            // ÉTAPE 2 : Envoyer une notification à tous les utilisateurs
            log.info("📤 [ÉTAPE 2] Envoi de notification à tous les utilisateurs...");

            // Récupérer tous les tokens FCM de tous les utilisateurs
            // Note: On récupère tous les userIds depuis les tokens enregistrés
            sendNotificationToAllUsers(event);

            log.info("✅ [EVENTS] Traitement terminé avec succès pour eventId={}", eventData.getEventId());
            log.info(NotificationConstants.SEPARATOR);

        } catch (Exception e) {
            log.error("❌ [EVENTS] Erreur lors du traitement de l'événement créé: {}",
                    e.getMessage(), e);
            log.info(NotificationConstants.SEPARATOR);
        }
    }

    /**
     * Envoie une notification push à tous les utilisateurs ayant un token FCM
     */
    private void sendNotificationToAllUsers(Event event) {
        try {
            // Récupérer tous les tokens depuis la base de données
            // On utilise une méthode qui récupère tous les tokens disponibles
            List<Integer> allUserIds = tokenManagementService.getAllUserIdsWithTokens();

            if (allUserIds.isEmpty()) {
                log.warn("⚠️ Aucun utilisateur avec token FCM trouvé");
                return;
            }

            log.info("📱 Envoi notification nouvel événement à {} utilisateurs", allUserIds.size());

            // Envoyer la notification via Firebase
            firebaseNotificationService.sendNewEventNotification(
                    event.getId().intValue(),
                    event.getTitle(),
                    event.getDescription(),
                    allUserIds);

        } catch (Exception e) {
            log.error("❌ Erreur lors de l'envoi de notification à tous les utilisateurs: {}",
                    e.getMessage(), e);
        }
    }

    /**
     * Récupère les événements avec pagination
     */
    public Page<Event> getEvents(Pageable pageable) {
        return eventRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    /**
     * Récupère les 20 derniers événements
     */
    public Page<Event> getLatestEvents(Pageable pageable) {
        return eventRepository.findTop20ByOrderByCreatedAtDesc(pageable);
    }

    /**
     * Récupère un événement par ID
     */
    public Event getEventById(Long eventId) {
        return eventRepository.findById(eventId).orElse(null);
    }
}

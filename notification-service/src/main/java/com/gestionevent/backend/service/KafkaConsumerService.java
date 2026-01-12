package com.gestionevent.backend.service;

import com.gestionevent.backend.event.EventCreatedEvent;
import com.gestionevent.backend.event.UserRegisteredEvent;
import com.gestionevent.backend.util.NotificationConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Service principal pour consommer les messages Kafka
 * Microservice Notifications / Notifications History
 */
@Service
public class KafkaConsumerService {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerService.class);

    @Autowired
    private FirebaseNotificationService firebaseService;

    /**
     * 📥 ÉCOUTER : Utilisateur inscrit à un événement
     * Topic: "registrations.created"
     * Le message est automatiquement désérialisé en UserRegisteredEvent grâce à la
     * configuration Kafka
     * 
     * Processus :
     * 1. Réception du message Kafka
     * 2. Validation des données
     * 3. Création de la notification en DB
     * 4. Envoi via FCM
     * 5. Mise à jour de la notification en DB avec les résultats
     */
    @KafkaListener(topics = "registrations.created", groupId = "notification-service", containerFactory = "registrationListenerFactory")
    public void consumeUserRegistered(UserRegisteredEvent event) {
        log.info(NotificationConstants.SEPARATOR);
        log.info("📥 [KAFKA] Réception message du topic 'registrations.created'");
        log.info("   └─ userId: {}", event.getUserId());
        log.info("   └─ eventId: {}", event.getEventId());
        log.info("   └─ eventTitle: '{}'", event.getEventTitle());
        log.info("   └─ registrationDate: {}", event.getRegistrationDate());

        try {
            // ÉTAPE 1 : Validation des données reçues
            log.info("🔍 [ÉTAPE 1] Validation des données du message...");

            if (event.getUserId() == null || event.getEventId() == null) {
                log.error("❌ [ÉTAPE 1] Message invalide: userId ou eventId manquant");
                log.error("   └─ userId: {}", event.getUserId());
                log.error("   └─ eventId: {}", event.getEventId());
                return;
            }

            if (event.getEventTitle() == null || event.getEventTitle().trim().isEmpty()) {
                log.warn("⚠️ [ÉTAPE 1] eventTitle est vide ou null, utilisation d'une valeur par défaut");
            }

            log.info("✅ [ÉTAPE 1] Données validées avec succès");

            // ÉTAPE 2 : Préparation de la date d'inscription
            String registrationDateStr = event.getRegistrationDate() != null ? event.getRegistrationDate().toString()
                    : null;

            log.info("🚀 [ÉTAPE 2] Démarrage du processus d'envoi de notification");
            log.info("   └─ user {} -> event {} '{}'",
                    event.getUserId(), event.getEventId(), event.getEventTitle());

            // ÉTAPE 3 : Envoi de la notification (création DB + envoi FCM + mise à jour DB)
            firebaseService.sendRegistrationNotification(
                    event.getUserId(),
                    event.getEventId(),
                    event.getEventTitle(),
                    registrationDateStr);

            log.info("✅ [KAFKA] Traitement terminé avec succès pour user {} et event {}",
                    event.getUserId(), event.getEventId());
            log.info(NotificationConstants.SEPARATOR);

        } catch (Exception e) {
            log.error("❌ [KAFKA] Erreur lors du traitement du message Kafka [registrations.created]");
            log.error("   └─ userId: {}", event.getUserId());
            log.error("   └─ eventId: {}", event.getEventId());
            log.error("   └─ Erreur: {}", e.getMessage(), e);
            log.info(NotificationConstants.SEPARATOR);
        }
    }

    /**
     * 📥 ÉCOUTER : Nouvel événement créé
     * Topic: "events.created"
     * Le message est automatiquement désérialisé en EventCreatedEvent grâce à la
     * configuration Kafka
     * 
     * Processus :
     * 1. Enregistrer l'événement dans la table `events` (localement)
     * 2. Envoyer une notification push à tous les utilisateurs ayant un token FCM
     */
    @KafkaListener(topics = "events.created", groupId = "notification-service", containerFactory = "eventListenerFactory")
    public void consumeEventCreated(EventCreatedEvent event) {
        log.info(NotificationConstants.SEPARATOR);
        log.info("📥 [EVENTS] Réception message du topic 'events.created'");
        log.info("   └─ eventId: {}", event.getEventId());
        log.info("   └─ eventTitle: '{}'", event.getEventTitle());
        log.info("   └─ eventDescription: {}...",
                event.getEventDescription() != null
                        ? event.getEventDescription().substring(0, Math.min(50, event.getEventDescription().length()))
                        : "null");
        log.info("   └─ creatorId: {}", event.getCreatorId());
        log.info("   └─ eventDate: {}", event.getEventDate());

        try {
            // Validation des données
            if (event.getEventId() == null || event.getEventTitle() == null || event.getEventTitle().trim().isEmpty()) {
                log.error("❌ [EVENTS] Message invalide: eventId ou eventTitle manquant");
                log.info("═══════════════════════════════════════════════════════════");
                return;
            }

            // Traiter l'événement (enregistrement en DB + notification à tous les
            // utilisateurs)
            firebaseService.processNewEvent(event);

        } catch (Exception e) {
            log.error("❌ [EVENTS] Erreur lors du traitement du message Kafka [events.created]");
            log.error("   └─ eventId: {}", event.getEventId());
            log.error("   └─ Erreur: {}", e.getMessage(), e);
            log.info(NotificationConstants.SEPARATOR);
        }
    }
}
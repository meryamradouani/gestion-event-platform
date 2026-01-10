package com.gestionevent.backend.service;

import com.gestionevent.backend.event.UserTokenUpdatedEvent;
import com.gestionevent.backend.util.NotificationConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Service pour consommer les messages Kafka du topic user.tokens.updated
 * Microservice Auth / Notifications Tokens
 */
@Service
public class TokenConsumerService {

    private static final Logger log = LoggerFactory.getLogger(TokenConsumerService.class);

    @Autowired
    private TokenManagementService tokenManagementService;

    /**
     * 📥 ÉCOUTER : Mise à jour de token FCM pour un utilisateur
     * Topic: "user.tokens.updated"
     * Le message est automatiquement désérialisé en UserTokenUpdatedEvent grâce à
     * la configuration Kafka
     */
    @KafkaListener(topics = "user.tokens.updated", groupId = "notification-service", containerFactory = "tokenListenerFactory")
    public void consumeUserTokenUpdated(UserTokenUpdatedEvent event) {
        log.info(NotificationConstants.SEPARATOR);
        log.info("📥 [TOKENS] Réception message du topic 'user.tokens.updated'");
        log.info("   └─ userId: {}", event.getUserId());
        log.info("   └─ fcmToken: {}...",
                event.getFcmToken() != null
                        ? event.getFcmToken().substring(0, Math.min(20, event.getFcmToken().length()))
                        : "null");
        log.info("   └─ deviceType: {}", event.getDeviceType());
        log.info("   └─ deviceInfo: {}", event.getDeviceInfo());
        log.info("   └─ createdAt: {}", event.getCreatedAt());
        log.info("   └─ updatedAt: {}", event.getUpdatedAt());

        try {
            // Validation des données
            if (event.getUserId() == null || event.getFcmToken() == null || event.getFcmToken().trim().isEmpty()) {
                log.error("❌ [TOKENS] Message invalide: userId ou fcmToken manquant");
                log.info(NotificationConstants.SEPARATOR);
                return;
            }

            log.info("💾 [TOKENS] Sauvegarde/mise à jour du token en DB...");

            // Sauvegarder ou mettre à jour le token dans la table notifications_tokens
            tokenManagementService.saveOrUpdateTokenFromEvent(
                    event.getUserId(),
                    event.getFcmToken(),
                    event.getDeviceType(),
                    event.getDeviceInfo());

            log.info("✅ [TOKENS] Token sauvegardé/mis à jour avec succès pour user {}", event.getUserId());
            log.info(NotificationConstants.SEPARATOR);

        } catch (Exception e) {
            log.error("❌ [TOKENS] Erreur lors du traitement du message Kafka [user.tokens.updated]");
            log.error("   └─ userId: {}", event.getUserId());
            log.error("   └─ Erreur: {}", e.getMessage(), e);
            log.info(NotificationConstants.SEPARATOR);
        }
    }
}

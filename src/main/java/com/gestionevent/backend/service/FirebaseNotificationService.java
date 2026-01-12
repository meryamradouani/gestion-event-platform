package com.gestionevent.backend.service;

import com.gestionevent.backend.event.EventCreatedEvent;
import com.gestionevent.backend.model.Event;
import com.gestionevent.backend.model.NotificationHistory;
import com.gestionevent.backend.repository.EventRepository;
import com.gestionevent.backend.repository.NotificationHistoryRepository;
import com.google.firebase.messaging.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FirebaseNotificationService {
    
    private static final Logger log = LoggerFactory.getLogger(FirebaseNotificationService.class);
    
    @Autowired
    private NotificationHistoryRepository historyRepository;
    
    @Autowired
    private TokenManagementService tokenService;
    
    @Autowired
    private EventRepository eventRepository;
    
    /**
     * 🔥 ENVOYER NOTIFICATION : Nouvel événement (à plusieurs users)
     * @param eventId ID de l'événement
     * @param eventTitle Titre de l'événement
     * @param eventDescription Description de l'événement
     * @param userIds Liste des IDs des utilisateurs à notifier
     */
    public void sendNewEventNotification(Integer eventId, String eventTitle, 
                                        String eventDescription, List<Integer> userIds) {
        String title = "🎉 Nouvel événement disponible !";
        String body = eventTitle;
        
        // Récupérer les tokens FCM de tous les users
        List<String> tokens = tokenService.getTokensForUsers(userIds);
        
        if (tokens.isEmpty()) {
            log.warn("Aucun token FCM trouvé pour les utilisateurs: {}", userIds);
            return;
        }
        
        log.info("Envoi notification nouvel événement '{}' à {} utilisateurs ({} tokens)", 
                eventTitle, userIds.size(), tokens.size());
        
        try {
            // Préparer les données supplémentaires
            Map<String, String> data = new HashMap<>();
            data.put("type", "NEW_EVENT");
            data.put("eventId", String.valueOf(eventId));
            data.put("eventTitle", eventTitle);
            data.put("eventDescription", eventDescription);
            data.put("click_action", "com.gestionevent.OPEN_EVENT_DETAILS");
            data.put("screen", "/event-details");
            
            // Créer le message multicast
            MulticastMessage message = MulticastMessage.builder()
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            // Image supprimée pour le support natif Android basic
                            .build())
                    .putAllData(data)
                    .addAllTokens(tokens)
                    .setAndroidConfig(AndroidConfig.builder()
                            .setPriority(AndroidConfig.Priority.HIGH)
                            .setNotification(AndroidNotification.builder()
                                    .setIcon("ic_notification")
                                    .setColor("#FF5722")
                                    .setClickAction("com.gestionevent.OPEN_EVENT_DETAILS")
                                    .build())
                            .build())
                    .setApnsConfig(ApnsConfig.builder()
                            .setAps(Aps.builder()
                                    .setBadge(1)
                                    .setSound("default")
                                    .build())
                            .build())
                    .build();
            
            // Envoyer les notifications
            BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(message);
            
            // Sauvegarder l'historique pour chaque utilisateur
            saveBatchNotificationHistory(userIds, title, body, 
                    NotificationHistory.NotificationType.NEW_EVENT, eventId, response);
            
            log.info("✅ Notification envoyée avec succès: {} réussis, {} échecs",
                    response.getSuccessCount(), response.getFailureCount());
            
            // Log des échecs détaillés
            if (response.getFailureCount() > 0) {
                List<SendResponse> responses = response.getResponses();
                for (int i = 0; i < responses.size(); i++) {
                    if (!responses.get(i).isSuccessful()) {
                        log.warn("Échec pour le token {}: {}", 
                                tokens.get(i).substring(0, Math.min(20, tokens.get(i).length())),
                                responses.get(i).getException().getMessage());
                    }
                }
            }
            
        } catch (FirebaseMessagingException e) {
            log.error("❌ Erreur Firebase pour l'événement {}: {}", eventId, e.getMessage(), e);
            
            // Sauvegarder l'échec dans l'historique pour tous les users
            saveFailedNotificationHistory(userIds, title, body, 
                    NotificationHistory.NotificationType.NEW_EVENT, eventId, e.getMessage());
        }
    }
    
    /**
     * 🔥 ENVOYER NOTIFICATION : Nouvelle inscription à un événement
     * Processus :
     * 1. Créer l'entrée en DB avec sent_successfully = false
     * 2. Envoyer la notification via FCM
     * 3. Mettre à jour l'entrée avec les résultats (succès ou échec)
     * 
     * @param userId ID de l'utilisateur
     * @param eventId ID de l'événement
     * @param eventTitle Titre de l'événement
     * @param eventDate Date de l'événement (optionnel)
     */
    public void sendRegistrationNotification(Integer userId, Integer eventId, 
                                           String eventTitle, String eventDate) {
        // Configuration selon les spécifications
        String title = "Nouvelle inscription à un événement";
        String body = eventTitle != null ? eventTitle : "Événement sans titre";
        
        log.info("📝 [ÉTAPE 1] Création de la notification en DB pour user {} et event {}", 
                userId, eventId);
        
        // ÉTAPE 1 : Créer l'entrée en DB AVANT l'envoi (sent_successfully = false)
        NotificationHistory history = new NotificationHistory(
                userId, 
                title, 
                body, 
                NotificationHistory.NotificationType.REGISTRATION, 
                eventId
        );
        // Initialiser avec sent_successfully = false
        history.setSentSuccessfully(false);
        history = historyRepository.save(history);
        
        log.info("✅ [ÉTAPE 1] Notification créée en DB avec ID: {}", history.getId());
        
        // Récupérer les tokens FCM de l'utilisateur (peut avoir plusieurs devices)
        List<String> tokens = tokenService.getTokensForUser(userId);
        
        if (tokens.isEmpty()) {
            log.warn("⚠️ [ÉTAPE 2] Aucun token FCM trouvé pour l'utilisateur: {}", userId);
            
            // Mettre à jour avec l'erreur
            history.markAsFailed("Aucun token FCM trouvé pour l'utilisateur");
            historyRepository.save(history);
            log.info("📝 [ÉTAPE 3] Notification mise à jour en DB avec erreur: aucun token");
            return;
        }
        
        log.info("📤 [ÉTAPE 2] Envoi notification inscription à user {} pour événement '{}' ({} device(s))", 
                userId, eventTitle, tokens.size());
        
        boolean atLeastOneSuccess = false;
        String lastFcmMessageId = null;
        String lastErrorMessage = null;
        
        // Envoyer à chaque device de l'utilisateur
        for (String token : tokens) {
            try {
                log.debug("📱 Tentative d'envoi à device: {}...", 
                        token.substring(0, Math.min(10, token.length())));
                
                // Préparer les données supplémentaires
                Map<String, String> data = new HashMap<>();
                data.put("type", "REGISTRATION_CREATED");
                data.put("eventId", String.valueOf(eventId));
                data.put("eventTitle", eventTitle);
                if (eventDate != null) {
                    data.put("eventDate", eventDate);
                }
                data.put("click_action", "com.gestionevent.OPEN_MY_REGISTRATIONS");
                data.put("screen", "/my-registrations");
                
                // Créer le message
                Message message = Message.builder()
                        .setNotification(Notification.builder()
                                .setTitle(title)
                                .setBody(body)
                                .build())
                        .putAllData(data)
                        .setToken(token)
                        .setAndroidConfig(AndroidConfig.builder()
                                .setPriority(AndroidConfig.Priority.HIGH)
                                .setNotification(AndroidNotification.builder()
                                        .setIcon("ic_notification")
                                        .setColor("#FF5722")
                                        .setClickAction("com.gestionevent.OPEN_MY_REGISTRATIONS")
                                        .build())
                                .build())
                        .setApnsConfig(ApnsConfig.builder()
                                .setAps(Aps.builder()
                                        .setBadge(1)
                                        .setSound("default")
                                        .build())
                                .build())
                        .build();
                
                // ÉTAPE 2 : Envoyer la notification via FCM
                String messageId = FirebaseMessaging.getInstance().send(message);
                lastFcmMessageId = messageId;
                atLeastOneSuccess = true;
                
                log.info("✅ [ÉTAPE 2] Notification envoyée avec succès à device {} (FCM Message ID: {})", 
                        token.substring(0, Math.min(10, token.length())), messageId);
                
            } catch (FirebaseMessagingException e) {
                lastErrorMessage = e.getMessage();
                log.error("❌ [ÉTAPE 2] Erreur Firebase pour device {}: {}", 
                        token.substring(0, Math.min(10, token.length())), e.getMessage());
            }
        }
        
        // ÉTAPE 3 : Mettre à jour l'entrée en DB avec les résultats
        log.info("📝 [ÉTAPE 3] Mise à jour de la notification en DB avec les résultats...");
        
        if (atLeastOneSuccess) {
            // Au moins un envoi a réussi
            history.setSentSuccessfully(true);
            history.setFcmMessageId(lastFcmMessageId);
            history.setErrorMessage(null);
            historyRepository.save(history);
            log.info("✅ [ÉTAPE 3] Notification mise à jour en DB: sent_successfully=true, fcm_message_id={}", 
                    lastFcmMessageId);
        } else {
            // Tous les envois ont échoué
            history.markAsFailed(lastErrorMessage != null ? lastErrorMessage : "Erreur inconnue lors de l'envoi");
            historyRepository.save(history);
            log.error("❌ [ÉTAPE 3] Notification mise à jour en DB: sent_successfully=false, error_message={}", 
                    lastErrorMessage);
        }
        
        log.info("✅ Processus terminé pour user {} et event {}", userId, eventId);
    }
    
    /**
     * 🔥 ENVOYER NOTIFICATION : Rappel événement (24h avant)
     * @param userId ID de l'utilisateur
     * @param eventId ID de l'événement
     * @param eventTitle Titre de l'événement
     * @param eventTime Heure de l'événement
     */
    public void sendEventReminder(Integer userId, Integer eventId, 
                                 String eventTitle, String eventTime) {
        String title = "⏰ Rappel événement";
        String body = "Demain: " + eventTitle + " à " + eventTime;
        
        List<String> tokens = tokenService.getTokensForUser(userId);
        
        if (tokens.isEmpty()) {
            return;
        }
        
        for (String token : tokens) {
            try {
                Map<String, String> data = new HashMap<>();
                data.put("type", "EVENT_REMINDER");
                data.put("eventId", String.valueOf(eventId));
                data.put("eventTitle", eventTitle);
                data.put("eventTime", eventTime);
                data.put("click_action", "com.gestionevent.OPEN_EVENT_DETAILS");
                
                Message message = Message.builder()
                        .setNotification(Notification.builder()
                                .setTitle(title)
                                .setBody(body)
                                .build())
                        .putAllData(data)
                        .setToken(token)
                        .setAndroidConfig(AndroidConfig.builder()
                                .setPriority(AndroidConfig.Priority.HIGH)
                                .setNotification(AndroidNotification.builder()
                                        .setIcon("ic_notification")
                                        .setColor("#FF9800")
                                        .setClickAction("com.gestionevent.OPEN_EVENT_DETAILS")
                                        .build())
                                .build())
                        .build();
                
                String messageId = FirebaseMessaging.getInstance().send(message);
                
                NotificationHistory history = new NotificationHistory(
                        userId, title, body, 
                        NotificationHistory.NotificationType.EVENT_REMINDER_24H, 
                        eventId
                );
                history.setFcmMessageId(messageId);
                historyRepository.save(history);
                
            } catch (FirebaseMessagingException e) {
                log.error("Erreur rappel pour user {}: {}", userId, e.getMessage());
                
                NotificationHistory history = new NotificationHistory(
                        userId, title, body, 
                        NotificationHistory.NotificationType.EVENT_REMINDER_24H, 
                        eventId
                );
                history.markAsFailed(e.getMessage());
                historyRepository.save(history);
            }
        }
    }
    
    /**
     * 💾 SAUVEGARDER L'HISTORIQUE POUR UN ENVOI MULTIPLE
     */
    private void saveBatchNotificationHistory(List<Integer> userIds, String title, String body,
                                             NotificationHistory.NotificationType type, Integer eventId,
                                             BatchResponse response) {
        List<NotificationHistory> histories = new ArrayList<>();
        
        for (int i = 0; i < userIds.size(); i++) {
            NotificationHistory history = new NotificationHistory(
                    userIds.get(i), title, body, type, eventId
            );
            
            // Vérifier si cet index a réussi
            if (i < response.getResponses().size()) {
                SendResponse sendResponse = response.getResponses().get(i);
                if (sendResponse.isSuccessful()) {
                    history.setFcmMessageId(sendResponse.getMessageId());
                } else {
                    history.markAsFailed(sendResponse.getException().getMessage());
                }
            }
            
            histories.add(history);
        }
        
        historyRepository.saveAll(histories);
    }
    
    /**
     * 💾 SAUVEGARDER LES ÉCHECS DANS L'HISTORIQUE
     */
    private void saveFailedNotificationHistory(List<Integer> userIds, String title, String body,
                                              NotificationHistory.NotificationType type, Integer eventId,
                                              String errorMessage) {
        List<NotificationHistory> histories = new ArrayList<>();
        
        for (Integer userId : userIds) {
            NotificationHistory history = new NotificationHistory(
                    userId, title, body, type, eventId
            );
            history.markAsFailed(errorMessage);
            histories.add(history);
        }
        
        historyRepository.saveAll(histories);
    }
    
    /**
     * 🔧 VALIDER UN TOKEN FCM
     * @param fcmToken Le token à valider
     * @return true si le token est valide
     */
    public boolean validateToken(String fcmToken) {
        try {
            // Firebase peut valider un token en essayant de l'envoyer à un topic fictif
            Message message = Message.builder()
                    .setToken(fcmToken)
                    .putData("validation", "true")
                    .build();
            
            FirebaseMessaging.getInstance().send(message);
            return true;
        } catch (FirebaseMessagingException e) {
            log.warn("Token FCM invalide: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 📥 TRAITER : Nouvel événement créé depuis Kafka
     * Processus :
     * 1. Enregistrer l'événement dans la table `events` (localement)
     * 2. Envoyer une notification push à tous les utilisateurs ayant un token FCM
     * 
     * @param eventData Données de l'événement depuis Kafka
     */
    @Transactional
    public void processNewEvent(EventCreatedEvent eventData) {
        log.info("📝 [ÉTAPE 1] Enregistrement de l'événement en DB...");
        
        try {
            // ÉTAPE 1 : Enregistrer l'événement dans la table `events`
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
            
            // Récupérer tous les userIds qui ont un token FCM
            List<Integer> allUserIds = tokenService.getAllUserIdsWithTokens();
            
            if (allUserIds.isEmpty()) {
                log.warn("⚠️ [ÉTAPE 2] Aucun utilisateur avec token FCM trouvé");
                return;
            }
            
            log.info("📱 [ÉTAPE 2] Envoi notification nouvel événement à {} utilisateurs", allUserIds.size());
            
            // Envoyer la notification via Firebase
            sendNewEventNotification(
                    event.getId().intValue(),
                    event.getTitle(),
                    event.getDescription(),
                    allUserIds
            );
            
            log.info("✅ [EVENTS] Traitement terminé avec succès pour eventId={}", eventData.getEventId());
            
        } catch (Exception e) {
            log.error("❌ [EVENTS] Erreur lors du traitement de l'événement créé: {}", 
                    e.getMessage(), e);
            throw e;
        }
    }
}
package com.gestionevent.backend.service;

import com.gestionevent.backend.dto.response.NotificationResponse;
import com.gestionevent.backend.dto.response.UnreadCountResponse;
import com.gestionevent.backend.model.NotificationHistory;
import com.gestionevent.backend.repository.NotificationHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service pour gérer les requêtes liées aux notifications.
 * Récupération, comptage et mise à jour du statut de lecture.
 */
@Service
public class NotificationQueryService {

    @Autowired
    private NotificationHistoryRepository historyRepository;

    /**
     * Récupère toutes les notifications d'un utilisateur avec pagination.
     * Les notifications sont triées par date de création décroissante (les plus récentes en premier).
     *
     * @param userId  L'ID de l'utilisateur
     * @param pageable Informations de pagination (page, taille, tri)
     * @return Page de NotificationResponse (DTO)
     */
    public Page<NotificationResponse> getUserNotifications(Integer userId, Pageable pageable) {
        Page<NotificationHistory> notifications = historyRepository
                .findByUserIdOrderByCreatedAtDesc(userId, pageable);

        // Convertit les entités NotificationHistory en DTO NotificationResponse
        return notifications.map(NotificationResponse::fromEntity);
    }

    /**
     * Récupère toutes les notifications non lues d'un utilisateur.
     * Utile pour afficher un badge ou liste des notifications non lues.
     *
     * @param userId L'ID de l'utilisateur
     * @return Liste de NotificationResponse
     */
    public List<NotificationResponse> getUnreadNotifications(Integer userId) {
        List<NotificationHistory> notifications = historyRepository.findByUserIdAndIsReadFalse(userId);

        // Convertit les entités en DTO pour l'affichage
        return notifications.stream()
                .map(NotificationResponse::fromEntity)
                .toList();
    }

    /**
     * Récupère le nombre de notifications non lues pour un utilisateur.
     * Utile pour afficher le badge compteur dans l'application.
     *
     * @param userId L'ID de l'utilisateur
     * @return UnreadCountResponse contenant l'ID de l'utilisateur et le nombre de notifications non lues
     */
    public UnreadCountResponse getUnreadCount(Integer userId) {
        int count = historyRepository.countByUserIdAndIsReadFalse(userId);
        return new UnreadCountResponse(userId, count);
    }

    /**
     * Marque une notification spécifique comme lue.
     * La transaction assure que la mise à jour est appliquée correctement.
     *
     * @param notificationId L'ID de la notification à marquer comme lue
     */
    @Transactional
    public void markAsRead(Long notificationId) {
        historyRepository.markAsRead(notificationId);
    }

    /**
     * Marque toutes les notifications d'un utilisateur comme lues.
     * Utile lorsque l'utilisateur clique sur "Tout marquer comme lu".
     *
     * @param userId L'ID de l'utilisateur
     */
    @Transactional
    public void markAllAsRead(Integer userId) {
        historyRepository.markAllAsRead(userId);
    }
}

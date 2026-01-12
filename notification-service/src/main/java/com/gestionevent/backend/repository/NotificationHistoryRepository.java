package com.gestionevent.backend.repository;

import com.gestionevent.backend.model.NotificationHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface NotificationHistoryRepository extends JpaRepository<NotificationHistory, Long> {
    
    // 🔹 Historique complet d'un user (pour le centre de notifs)
    Page<NotificationHistory> findByUserIdOrderByCreatedAtDesc(Integer userId, Pageable pageable);
    
    // 🔹 Notifications non lues (badge counter)
    List<NotificationHistory> findByUserIdAndIsReadFalse(Integer userId);
    
    // 🔹 Compter les non lues (pour badge)
    int countByUserIdAndIsReadFalse(Integer userId);
    
    // 🔹 Marquer une notif comme lue (quand user clique dessus)
    @Transactional
    @Modifying
    @Query("UPDATE NotificationHistory nh SET nh.isRead = true, nh.readAt = CURRENT_TIMESTAMP WHERE nh.id = :id")
    void markAsRead(@Param("id") Long id);
    
    // 🔹 Marquer TOUTES les notifs comme lues (bouton "Tout marquer comme lu")
    @Transactional
    @Modifying
    @Query("UPDATE NotificationHistory nh SET nh.isRead = true, nh.readAt = CURRENT_TIMESTAMP WHERE nh.userId = :userId AND nh.isRead = false")
    void markAllAsRead(@Param("userId") Integer userId);
    
    // 🔹 Notifs liées à un événement (pour debug)
    List<NotificationHistory> findByEventId(Integer eventId);
    
    // 🔹 Supprimer l'historique d'un user (optionnel)
    @Transactional
    void deleteByUserId(Integer userId);
}
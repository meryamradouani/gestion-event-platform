package com.gestionevent.backend.repository;

import com.gestionevent.backend.model.NotificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationTokenRepository extends JpaRepository<NotificationToken, Long> {
    
    // 🔹 Trouver TOUS les tokens d'un utilisateur (pour envoyer sur tous ses devices)
    List<NotificationToken> findByUserId(Integer userId);
    
    // 🔹 Trouver un token spécifique (pour mise à jour/suppression)
    Optional<NotificationToken> findByUserIdAndFcmToken(Integer userId, String fcmToken);
    
    // 🔹 Trouver les tokens de PLUSIEURS utilisateurs (pour notif de groupe)
    List<NotificationToken> findByUserIdIn(List<Integer> userIds);
    
    // 🔹 Vérifier si un token existe (éviter les doublons)
    boolean existsByUserIdAndFcmToken(Integer userId, String fcmToken);
    
    // 🔹 Supprimer un token (quand user se déconnecte ou réinstalle l'app)
    @Transactional
    void deleteByUserIdAndFcmToken(Integer userId, String fcmToken);
}
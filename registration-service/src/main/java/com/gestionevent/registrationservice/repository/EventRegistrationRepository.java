package com.gestionevent.registrationservice.repository;

import com.gestionevent.registrationservice.model.EventRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRegistrationRepository
        extends JpaRepository<EventRegistration, Long> {

    // Compter inscriptions pour un événement
    Long countByEventId(Long eventId);

    // Vérifier si utilisateur déjà inscrit
    boolean existsByUserIdAndEventId(Long userId, Long eventId);

    // Trouver inscription spécifique
    Optional<EventRegistration> findByUserIdAndEventId(Long userId, Long eventId);
    // Lister inscriptions d'un événement (pour organisateur)
    List<EventRegistration> findByEventId(Long eventId);
    // Lister inscriptions d'un utilisateur
    List<EventRegistration> findByUserId(Long userId);

    // Supprimer robuste (gère les doublons potentiels)
    void deleteByUserIdAndEventId(Long userId, Long eventId);
}
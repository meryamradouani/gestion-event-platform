package com.gestionevent.backend.repository;

import com.gestionevent.backend.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    
    /**
     * Récupère les événements triés par date de création décroissante
     */
    Page<Event> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    /**
     * Récupère les événements actifs triés par date de création décroissante
     */
    Page<Event> findByStatusOrderByCreatedAtDesc(String status, Pageable pageable);
    
    /**
     * Récupère les 20 derniers événements
     */
    @Query("SELECT e FROM Event e ORDER BY e.createdAt DESC")
    Page<Event> findTop20ByOrderByCreatedAtDesc(Pageable pageable);
}


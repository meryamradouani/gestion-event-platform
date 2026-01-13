package com.gestionevent.registrationservice.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;

@Entity
@Table(name = "event_registrations", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "event_id"})
})
@Data
public class EventRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "registered_at")
    private LocalDateTime registeredAt;

    // Constructeurs
    public EventRegistration() {
        this.registeredAt = LocalDateTime.now();
    }

    public EventRegistration(Long userId, Long eventId) {
        this();
        this.userId = userId;
        this.eventId = eventId;
    }
}
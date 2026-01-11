package com.gestionevent.registrationservice.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;

@Entity
@Table(name = "event_registrations")
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

    @Column(name = "attendance_status")
    private String attendanceStatus;

    @Column(name = "qr_code")
    private String qrCode;

    // Constructeurs
    public EventRegistration() {
        this.registeredAt = LocalDateTime.now();
        this.attendanceStatus = "pending";
    }

    public EventRegistration(Long userId, Long eventId) {
        this();
        this.userId = userId;
        this.eventId = eventId;
    }
}
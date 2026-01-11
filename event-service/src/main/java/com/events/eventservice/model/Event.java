package com.events.eventservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le titre est obligatoire")
    @Size(min = 3, max = 255, message = "Le titre doit contenir entre 3 et 255 caractères")
    private String title;

    @Size(max = 2000, message = "La description ne doit pas dépasser 2000 caractères")
    private String description;

    @Future(message = "La date de l'événement doit être dans le futur")
    @Column(name = "event_date")
    private LocalDateTime eventDate;

    @NotBlank(message = "Le lieu est obligatoire")
    private String location;

    @Min(value = 1, message = "Le nombre maximum de participants doit être au moins 1")
    @Max(value = 1000, message = "Le nombre maximum de participants ne peut pas dépasser 1000")
    @Column(name = "max_participants")
    private Integer maxParticipants;

    @Column(name = "created_by")
    @NotNull(message = "L'ID du créateur est obligatoire")
    private Long createdBy;

    @Enumerated(EnumType.STRING)
    private EventStatus status = com.events.eventservice.model.EventStatus.ACTIVE;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
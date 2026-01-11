package com.gestionevent.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
public class Event {
    
    @Id
    @Column(name = "id")
    private Long id;  // ID provenant du message Kafka eventId
    
    @Column(name = "title", nullable = false, length = 255)
    private String title;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "event_date")
    private LocalDateTime eventDate;
    
    @Column(name = "location", length = 255)
    private String location;
    
    @Column(name = "club_id")
    private Long clubId;
    
    @Column(name = "created_by")
    private Long createdBy;
    
    @Column(name = "status", length = 50)
    private String status = "actif";
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    // ========== CONSTRUCTEURS ==========
    public Event() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Event(Long id, String title, String description, LocalDateTime eventDate, 
                 Long createdBy) {
        this();
        this.id = id;
        this.title = title;
        this.description = description;
        this.eventDate = eventDate;
        this.createdBy = createdBy;
    }
    
    // ========== GETTERS ==========
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public LocalDateTime getEventDate() { return eventDate; }
    public String getLocation() { return location; }
    public Long getClubId() { return clubId; }
    public Long getCreatedBy() { return createdBy; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    
    // ========== SETTERS ==========
    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setEventDate(LocalDateTime eventDate) { this.eventDate = eventDate; }
    public void setLocation(String location) { this.location = location; }
    public void setClubId(Long clubId) { this.clubId = clubId; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    public void setStatus(String status) { this.status = status; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    // ========== TO STRING ==========
    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + (description != null ? description.substring(0, Math.min(50, description.length())) + "..." : "null") +
                ", eventDate=" + eventDate +
                ", createdBy=" + createdBy +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}


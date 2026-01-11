package com.gestionevent.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications_history")
public class NotificationHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Integer userId;
    
    @Column(nullable = false, length = 255)
    private String title;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String body;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private NotificationType type;
    
    @Column(name = "event_id")
    private Integer eventId;
    
    @Column(name = "sent_successfully", nullable = false)
    private Boolean sentSuccessfully = true;
    
    @Column(name = "fcm_message_id", length = 255)
    private String fcmMessageId;
    
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
    
    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;
    
    @Column(name = "read_at")
    private LocalDateTime readAt;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    // ========== ENUM POUR LES TYPES ==========
    public enum NotificationType {
        EVENT_REMINDER_24H("Rappel 24h avant"),
        EVENT_REMINDER_1H("Rappel 1h avant"),
        NEW_EVENT("event"),  // Pour les nouveaux événements
        REGISTRATION_CONFIRMED("Inscription confirmée"),
        REGISTRATION("registration"),  // Pour les inscriptions (utilisé pour registrations.created)
        REGISTRATION_CREATED("registration"),  // Alias pour REGISTRATION
        EVENT_CANCELLED("Événement annulé"),
        EVENT_UPDATED("Événement modifié"),
        SYSTEM("Message système"),
        CUSTOM("Notification personnalisée");
        
        private final String description;
        
        NotificationType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    // ========== CONSTRUCTEURS ==========
    public NotificationHistory() {
        this.createdAt = LocalDateTime.now();
    }
    
    public NotificationHistory(Integer userId, String title, String body, 
                               NotificationType type, Integer eventId) {
        this();
        this.userId = userId;
        this.title = title;
        this.body = body;
        this.type = type;
        this.eventId = eventId;
    }
    
    // ========== GETTERS ==========
    public Long getId() { return id; }
    public Integer getUserId() { return userId; }
    public String getTitle() { return title; }
    public String getBody() { return body; }
    public NotificationType getType() { return type; }
    public Integer getEventId() { return eventId; }
    public Boolean getSentSuccessfully() { return sentSuccessfully; }
    public String getFcmMessageId() { return fcmMessageId; }
    public String getErrorMessage() { return errorMessage; }
    public Boolean getIsRead() { return isRead; }
    public LocalDateTime getReadAt() { return readAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    
    // ========== SETTERS ==========
    public void setId(Long id) { this.id = id; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public void setTitle(String title) { this.title = title; }
    public void setBody(String body) { this.body = body; }
    public void setType(NotificationType type) { this.type = type; }
    public void setEventId(Integer eventId) { this.eventId = eventId; }
    public void setSentSuccessfully(Boolean sentSuccessfully) { this.sentSuccessfully = sentSuccessfully; }
    public void setFcmMessageId(String fcmMessageId) { this.fcmMessageId = fcmMessageId; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public void setIsRead(Boolean isRead) { 
        this.isRead = isRead;
        if (isRead && this.readAt == null) {
            this.readAt = LocalDateTime.now();
        }
    }
    
    public void setReadAt(LocalDateTime readAt) { this.readAt = readAt; }
    
    // ========== METHODES UTILITAIRES ==========
    public void markAsRead() {
        this.isRead = true;
        this.readAt = LocalDateTime.now();
    }
    
    public void markAsFailed(String error) {
        this.sentSuccessfully = false;
        this.errorMessage = error;
    }
    
    // ========== TO STRING ==========
    @Override
    public String toString() {
        return "NotificationHistory{" +
                "id=" + id +
                ", userId=" + userId +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", type=" + type +
                ", eventId=" + eventId +
                ", sentSuccessfully=" + sentSuccessfully +
                ", isRead=" + isRead +
                ", createdAt=" + createdAt +
                '}';
    }
}
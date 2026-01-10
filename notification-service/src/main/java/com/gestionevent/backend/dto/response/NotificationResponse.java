package com.gestionevent.backend.dto.response;

import com.gestionevent.backend.model.NotificationHistory;
import java.time.LocalDateTime;

public class NotificationResponse {
    private Long id;
    private String title;
    private String body;
    private String type;
    private String typeDescription;
    private Integer eventId;
    private boolean isRead;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;

    // Constructeur par défaut pour Jackson
    public NotificationResponse() {
    }

    public static NotificationResponse fromEntity(NotificationHistory entity) {
        if (entity == null) {
            return null;
        }

        NotificationResponse response = new NotificationResponse();
        response.setId(entity.getId());
        response.setTitle(entity.getTitle());
        response.setBody(entity.getBody());
        response.setType(entity.getType().name());
        response.setTypeDescription(entity.getType().getDescription());
        response.setEventId(entity.getEventId());
        response.setRead(entity.getIsRead());
        response.setReadAt(entity.getReadAt());
        response.setCreatedAt(entity.getCreatedAt());
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeDescription() {
        return typeDescription;
    }

    public void setTypeDescription(String typeDescription) {
        this.typeDescription = typeDescription;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
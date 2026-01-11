package com.example.dto;

// src/main/java/com/events/profile/dto/EventCreatedEvent.java

import java.time.LocalDateTime;

public class EventCreatedEvent {

    private Long eventId;
    private Long organizerId; // L'utilisateur qui a créé l'événement
    private String eventTitle;
    private String eventDescription; // UPDATED: Match event-service
    private LocalDateTime eventDate;
    private String location;
    private LocalDateTime creationTime;

    // Constructeur par défaut
    public EventCreatedEvent() {
        this.creationTime = LocalDateTime.now();
    }

    // Constructeur avec paramètres
    public EventCreatedEvent(Long eventId, Long organizerId, String eventTitle,
                             String eventDescription, LocalDateTime eventDate,
                             String location, LocalDateTime creationTime) {
        this.eventId = eventId;
        this.organizerId = organizerId;
        this.eventTitle = eventTitle;
        this.eventDescription = eventDescription;
        this.eventDate = eventDate;
        this.location = location;
        this.creationTime = creationTime;
    }

    // GETTERS
    public Long getEventId() {
        return eventId;
    }

    public Long getOrganizerId() {
        return organizerId;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public String getLocation() {
        return location;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    // SETTERS
    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public void setOrganizerId(Long organizerId) {
        this.organizerId = organizerId;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

    @Override
    public String toString() {
        return "EventCreatedEvent{" +
                "eventId=" + eventId +
                ", organizerId=" + organizerId +
                ", eventTitle='" + eventTitle + '\'' +
                ", eventDescription='" + eventDescription + '\'' +
                ", eventDate=" + eventDate +
                ", location='" + location + '\'' +
                ", creationTime=" + creationTime +
                '}';
    }
}
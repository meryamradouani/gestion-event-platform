package com.example.dto;

// src/main/java/com/events/profile/dto/RegistrationConfirmedEvent.java

import java.time.LocalDateTime;

public class RegistrationConfirmedEvent {

    private Long userId;
    private Long eventId;
    private String eventTitle;
    private LocalDateTime eventDate;
    private LocalDateTime registrationTime;

    // Constructeur par défaut
    public RegistrationConfirmedEvent() {
        this.registrationTime = LocalDateTime.now();
    }

    // Constructeur avec paramètres
    public RegistrationConfirmedEvent(Long userId, Long eventId, String eventTitle,
            LocalDateTime eventDate, LocalDateTime registrationTime) {
        this.userId = userId;
        this.eventId = eventId;
        this.eventTitle = eventTitle;
        this.eventDate = eventDate;
        this.registrationTime = registrationTime;
    }

    // GETTERS
    public Long getUserId() {
        return userId;
    }

    public Long getEventId() {
        return eventId;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public LocalDateTime getRegistrationTime() {
        return registrationTime;
    }

    // SETTERS
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }

    public void setRegistrationTime(LocalDateTime registrationTime) {
        this.registrationTime = registrationTime;
    }

    @Override
    public String toString() {
        return "RegistrationConfirmedEvent{" +
                "userId=" + userId +
                ", eventId=" + eventId +
                ", eventTitle='" + eventTitle + '\'' +
                ", eventDate=" + eventDate +
                ", registrationTime=" + registrationTime +
                '}';
    }
}
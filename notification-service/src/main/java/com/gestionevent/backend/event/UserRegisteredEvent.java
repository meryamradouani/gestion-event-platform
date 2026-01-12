package com.gestionevent.backend.event;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

/**
 * Événement représentant l'inscription d'un utilisateur à un événement.
 * Format JSON attendu:
 * {
 *   "userId": 789,
 *   "eventId": 123,
 *   "eventTitle": "Concert de Jazz",
 *   "registrationDate": "2024-12-20T14:30:00"
 * }
 */
public class UserRegisteredEvent {
    @JsonProperty("userId")
    private Integer userId;
    
    @JsonProperty("eventId")
    private Integer eventId;
    
    @JsonProperty("eventTitle")
    private String eventTitle;
    
    @JsonProperty("registrationDate")
    private LocalDateTime registrationDate;
    
    public UserRegisteredEvent() {}
    
    public UserRegisteredEvent(Integer userId, Integer eventId, String eventTitle, LocalDateTime registrationDate) {
        this.userId = userId;
        this.eventId = eventId;
        this.eventTitle = eventTitle;
        this.registrationDate = registrationDate;
    }
    
    public Integer getUserId() {
        return userId;
    }
    
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    
    public Integer getEventId() {
        return eventId;
    }
    
    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }
    
    public String getEventTitle() {
        return eventTitle;
    }
    
    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }
    
    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }
    
    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }
}
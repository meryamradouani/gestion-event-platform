package com.gestionevent.backend.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Événement représentant la création d'un événement.
 * Format JSON attendu:
 * {
 *   "eventId": 123,
 *   "eventTitle": "Concert de Jazz",
 *   "eventDescription": "Un concert exceptionnel...",
 *   "creatorId": 456,
 *   "eventDate": "2024-12-25T20:00:00"
 * }
 */
public class EventCreatedEvent {
    
    @JsonProperty("eventId")
    private Long eventId;
    
    @JsonProperty("eventTitle")
    private String eventTitle;
    
    @JsonProperty("eventDescription")
    private String eventDescription;
    
    @JsonProperty("creatorId")
    private Long creatorId;
    
    @JsonProperty("eventDate")
    private LocalDateTime eventDate;
    
    // Ancien champ pour compatibilité (optionnel)
    private List<Integer> userIdsToNotify;
    
    public EventCreatedEvent() {}
    
    public EventCreatedEvent(Long eventId, String eventTitle, String eventDescription, 
                            Long creatorId, LocalDateTime eventDate) {
        this.eventId = eventId;
        this.eventTitle = eventTitle;
        this.eventDescription = eventDescription;
        this.creatorId = creatorId;
        this.eventDate = eventDate;
    }
    
    public Long getEventId() {
        return eventId;
    }
    
    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }
    
    public String getEventTitle() {
        return eventTitle;
    }
    
    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }
    
    public String getEventDescription() {
        return eventDescription;
    }
    
    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }
    
    public Long getCreatorId() {
        return creatorId;
    }
    
    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }
    
    public List<Integer> getUserIdsToNotify() {
        return userIdsToNotify;
    }
    
    public void setUserIdsToNotify(List<Integer> userIdsToNotify) {
        this.userIdsToNotify = userIdsToNotify;
    }
    
    public LocalDateTime getEventDate() {
        return eventDate;
    }
    
    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }
}
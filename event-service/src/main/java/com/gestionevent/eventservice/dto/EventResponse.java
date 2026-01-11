package com.gestionevent.eventservice.dto;

import com.gestionevent.eventservice.model.EventStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class EventResponse {
    private Long id;
    private String title;
    private String description;
    private String imageUrl;
    private LocalDateTime eventDate;
    private String location;
    private Integer maxParticipants;
    private Long createdBy;
    private EventStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

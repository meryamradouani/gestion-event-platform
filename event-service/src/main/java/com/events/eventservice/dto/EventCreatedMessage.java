package com.events.eventservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventCreatedMessage {
    private Long eventId;
    private Long organizerId;
    private String eventTitle;
    private String eventDescription;
    private LocalDateTime eventDate;
    private String location;
    private LocalDateTime creationTime;
}
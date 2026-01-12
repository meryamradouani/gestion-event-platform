package com.events.eventservice.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateEventRequest {
    @NotBlank(message = "Le titre est obligatoire")
    @Size(min = 3, max = 255, message = "Le titre doit contenir entre 3 et 255 caractères")
    private String title;

    @Size(max = 2000, message = "La description ne doit pas dépasser 2000 caractères")
    private String description;

    @Future(message = "La date de l'événement doit être dans le futur")
    private LocalDateTime eventDate;

    @NotBlank(message = "Le lieu est obligatoire")
    private String location;

    @Min(value = 1, message = "Le nombre maximum de participants doit être au moins 1")
    @Max(value = 1000, message = "Le nombre maximum de participants ne peut pas dépasser 1000")
    private Integer maxParticipants;
}
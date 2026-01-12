package com.gestionevent.auth_service.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UserAuthenticatedMessage {
    // Champs de base (Communs)
    private Long userId;
    private String email;
    private String fullName;
    private String role; // "student" ou "organizer"
    private String loginTime;

    // Champs spécifiques aux Étudiants
    private String institution;
    private String major;

    // Champs spécifiques aux Organisateurs
    private String organizationName;
    private String organizationType;

    // Constructeur par défaut (Obligatoire pour la désérialisation JSON)
    public UserAuthenticatedMessage() {}

    // Constructeur pratique pour l'initialisation rapide
    public UserAuthenticatedMessage(Long userId, String email, String fullName, String role) {
        this.userId = userId;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        // Format ISO-8601 requis par Salma
        this.loginTime = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
    }

    // --- GETTERS ET SETTERS (Indispensables pour Kafka/Jackson) ---

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getLoginTime() { return loginTime; }
    public void setLoginTime(String loginTime) { this.loginTime = loginTime; }

    public String getInstitution() { return institution; }
    public void setInstitution(String institution) { this.institution = institution; }

    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }

    public String getOrganizationName() { return organizationName; }
    public void setOrganizationName(String organizationName) { this.organizationName = organizationName; }

    public String getOrganizationType() { return organizationType; }
    public void setOrganizationType(String organizationType) { this.organizationType = organizationType; }
}
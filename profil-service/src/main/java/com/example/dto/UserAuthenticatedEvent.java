package com.example.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserAuthenticatedEvent {

    private Long userId;
    private String email;
    private String fullName; // Optionnel
    private String role;     // Stocké dans user_type
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime loginTime;

    // Champs spécifiques
    private String institution;       // Pour étudiant
    private String major;             // Pour étudiant
    private String organizationName;  // Pour organisateur
    private String organizationType;  // Pour organisateur

    public UserAuthenticatedEvent() {}

    public UserAuthenticatedEvent(Long userId, String email, String fullName, String role, LocalDateTime loginTime,
                                  String institution, String major, String organizationName, String organizationType) {
        this.userId = userId;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.loginTime = loginTime;
        this.institution = institution;
        this.major = major;
        this.organizationName = organizationName;
        this.organizationType = organizationType;
    }

    // GETTERS & SETTERS
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public LocalDateTime getLoginTime() { return loginTime; }
    public void setLoginTime(LocalDateTime loginTime) { this.loginTime = loginTime; }
    public String getInstitution() { return institution; }
    public void setInstitution(String institution) { this.institution = institution; }
    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }
    public String getOrganizationName() { return organizationName; }
    public void setOrganizationName(String organizationName) { this.organizationName = organizationName; }
    public String getOrganizationType() { return organizationType; }
    public void setOrganizationType(String organizationType) { this.organizationType = organizationType; }
}
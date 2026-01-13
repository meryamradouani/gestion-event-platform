package com.gestionevent.auth_service.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UserAuthenticatedMessage {
    private Long userId;
    private String email;
    private String fullName;
    private String role;
    private String loginTime;
    
    // Role-specific fields
    private String institution;
    private String major;
    private String organizationName;
    private String organizationType;

    public UserAuthenticatedMessage() {}

    public UserAuthenticatedMessage(Long userId, String email, String fullName, String role) {
        this.userId = userId;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        // Format strict yyyy-MM-dd'T'HH:mm:ss requis par profil-service
        this.loginTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
    }

    // Comprehensive constructor
    public UserAuthenticatedMessage(Long userId, String email, String fullName, String role,
                                   String institution, String major, String organizationName, String organizationType) {
        this(userId, email, fullName, role);
        this.institution = institution;
        this.major = major;
        this.organizationName = organizationName;
        this.organizationType = organizationType;
    }

    // Getters et Setters
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
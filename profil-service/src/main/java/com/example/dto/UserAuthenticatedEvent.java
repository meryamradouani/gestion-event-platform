package com.example.dto;

// src/main/java/com/events/profile/dto/UserAuthenticatedEvent.java

import java.time.LocalDateTime;

public class UserAuthenticatedEvent {

    private Long userId;
    private String email;
    private String fullName; // NOUVEAU
    private String role; // "student" ou "organizer"
    private LocalDateTime loginTime;

    // Constructeur par défaut
    public UserAuthenticatedEvent() {
        this.loginTime = LocalDateTime.now();
    }

    // Constructeur avec paramètres
    public UserAuthenticatedEvent(Long userId, String email, String fullName, String role, LocalDateTime loginTime) {
        this.userId = userId;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.loginTime = loginTime;
    }

    // GETTERS
    public Long getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public String getRole() {
        return role;
    }

    public LocalDateTime getLoginTime() {
        return loginTime;
    }

    // SETTERS
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setLoginTime(LocalDateTime loginTime) {
        this.loginTime = loginTime;
    }

    @Override
    public String toString() {
        return "UserAuthenticatedEvent{" +
                "userId=" + userId +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", role='" + role + '\'' +
                ", loginTime=" + loginTime +
                '}';
    }
}
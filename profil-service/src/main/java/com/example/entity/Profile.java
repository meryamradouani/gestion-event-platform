package com.example.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "profiles", schema = "profile_db")
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Transient
    private String profilePicUrl;

    @Column(name = "user_id", unique = true, nullable = false)
    private Long userId;

    @Column(name = "user_type", length = 50)
    private String userType; // "STUDENT" or "ORGANIZER"

    @Column(name = "full_name", length = 150)
    private String fullName;

    @Column(name = "institution", length = 255)
    private String institution;

    @Column(name = "major", length = 255)
    private String major;

    @Column(name = "organization_name", length = 255)
    private String organizationName;

    @Column(name = "organization_type", length = 100)
    private String organizationType;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(name = "events_history", columnDefinition = "JSON")
    private String eventsHistory;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructeur par défaut
    public Profile() {}

    // Constructeur avec paramètres
    public Profile(Long userId, String fullName) {
        this.userId = userId;
        this.fullName = fullName;
    }

    // =========== GETTERS ===========
    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public String getBio() {
        return bio;
    }

    public String getEventsHistory() {
        return eventsHistory;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public String getUserType() {
        return userType;
    }

    public String getInstitution() {
        return institution;
    }

    public String getMajor() {
        return major;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public String getOrganizationType() {
        return organizationType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // =========== SETTERS ===========
    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setEventsHistory(String eventsHistory) {
        this.eventsHistory = eventsHistory;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public void setOrganizationType(String organizationType) {
        this.organizationType = organizationType;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
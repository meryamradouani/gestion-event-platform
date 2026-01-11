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

    @Column(name = "user_id", unique = true, nullable = false)
    private Long userId;

    @Column(name = "full_name", length = 150)
    private String fullName;

    @Column(name = "profile_pic_url", length = 255)
    private String profilePicUrl;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(columnDefinition = "JSON")
    private String favorites;

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
    public Profile() {
    }

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

    public String getFavorites() {
        return favorites;
    }

    public String getEventsHistory() {
        return eventsHistory;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
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

    public void setFavorites(String favorites) {
        this.favorites = favorites;
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

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
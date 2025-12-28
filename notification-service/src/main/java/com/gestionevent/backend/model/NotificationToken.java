package com.gestionevent.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications_tokens")
public class NotificationToken {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Integer userId;  // ID venant de Auth-Service
    
    @Column(name = "fcm_token", columnDefinition = "TEXT", nullable = false)
    private String fcmToken;  // Token Firebase du téléphone
    
    @Column(name = "device_type", length = 50)
    private String deviceType;  // "android", "ios"
    
    @Column(name = "device_info", length = 255)
    private String deviceInfo;  // "Samsung Galaxy S23", etc.
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // ========== CONSTRUCTEURS ==========
    public NotificationToken() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public NotificationToken(Integer userId, String fcmToken, String deviceType) {
        this();
        this.userId = userId;
        this.fcmToken = fcmToken;
        this.deviceType = deviceType;
    }
    
    // ========== GETTERS ==========
    public Long getId() { return id; }
    public Integer getUserId() { return userId; }
    public String getFcmToken() { return fcmToken; }
    public String getDeviceType() { return deviceType; }
    public String getDeviceInfo() { return deviceInfo; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    
    // ========== SETTERS ==========
    public void setId(Long id) { this.id = id; }
    public void setUserId(Integer userId) { this.userId = userId; }
    
    public void setFcmToken(String fcmToken) { 
        this.fcmToken = fcmToken;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void setDeviceType(String deviceType) { 
        this.deviceType = deviceType;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void setDeviceInfo(String deviceInfo) { 
        this.deviceInfo = deviceInfo;
        this.updatedAt = LocalDateTime.now();
    }
    
    // ========== TO STRING ==========
    @Override
    public String toString() {
        return "NotificationToken{" +
                "id=" + id +
                ", userId=" + userId +
                ", fcmToken='" + (fcmToken != null ? fcmToken.substring(0, Math.min(20, fcmToken.length())) + "..." : "null") +
                ", deviceType='" + deviceType + '\'' +
                ", deviceInfo='" + deviceInfo + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
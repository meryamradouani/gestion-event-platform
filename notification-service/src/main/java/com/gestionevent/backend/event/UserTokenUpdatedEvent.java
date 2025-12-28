package com.gestionevent.backend.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

/**
 * Événement représentant la mise à jour d'un token FCM pour un utilisateur.
 * Format JSON attendu:
 * {
 *   "userId": 123,
 *   "fcmToken": "abc123xyz",
 *   "deviceType": "android",
 *   "deviceInfo": "Samsung Galaxy S23",
 *   "createdAt": "2025-12-25T14:00:00",
 *   "updatedAt": "2025-12-25T14:00:00"
 * }
 */
public class UserTokenUpdatedEvent {
    
    @JsonProperty("userId")
    private Integer userId;
    
    @JsonProperty("fcmToken")
    private String fcmToken;
    
    @JsonProperty("deviceType")
    private String deviceType;
    
    @JsonProperty("deviceInfo")
    private String deviceInfo;
    
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;
    
    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;
    
    public UserTokenUpdatedEvent() {}
    
    public UserTokenUpdatedEvent(Integer userId, String fcmToken, String deviceType, 
                                 String deviceInfo, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.userId = userId;
        this.fcmToken = fcmToken;
        this.deviceType = deviceType;
        this.deviceInfo = deviceInfo;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // ========== GETTERS ==========
    public Integer getUserId() { return userId; }
    public String getFcmToken() { return fcmToken; }
    public String getDeviceType() { return deviceType; }
    public String getDeviceInfo() { return deviceInfo; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    
    // ========== SETTERS ==========
    public void setUserId(Integer userId) { this.userId = userId; }
    public void setFcmToken(String fcmToken) { this.fcmToken = fcmToken; }
    public void setDeviceType(String deviceType) { this.deviceType = deviceType; }
    public void setDeviceInfo(String deviceInfo) { this.deviceInfo = deviceInfo; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}


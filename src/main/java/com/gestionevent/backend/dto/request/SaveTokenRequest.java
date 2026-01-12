package com.gestionevent.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SaveTokenRequest {
    
    @NotNull(message = "L'ID utilisateur est obligatoire")
    private Integer userId;
    
    @NotBlank(message = "Le token FCM est obligatoire")
    private String fcmToken;
    
    private String deviceType;
    private String deviceInfo;
    
    public SaveTokenRequest() {}
    
    public SaveTokenRequest(Integer userId, String fcmToken, String deviceType, String deviceInfo) {
        this.userId = userId;
        this.fcmToken = fcmToken;
        this.deviceType = deviceType;
        this.deviceInfo = deviceInfo;
    }
    
    public Integer getUserId() {
        return userId;
    }
    
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    
    public String getFcmToken() {
        return fcmToken;
    }
    
    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
    
    public String getDeviceType() {
        return deviceType;
    }
    
    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }
    
    public String getDeviceInfo() {
        return deviceInfo;
    }
    
    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }
}
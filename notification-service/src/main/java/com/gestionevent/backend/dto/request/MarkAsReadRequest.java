package com.gestionevent.backend.dto.request;

public class MarkAsReadRequest {
    private Long notificationId;
    private boolean markAll;
    private Integer userId; // Pour markAll
    
    public MarkAsReadRequest() {}
    
    public MarkAsReadRequest(Long notificationId, boolean markAll, Integer userId) {
        this.notificationId = notificationId;
        this.markAll = markAll;
        this.userId = userId;
    }
    
    public Long getNotificationId() {
        return notificationId;
    }
    
    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }
    
    public boolean isMarkAll() {
        return markAll;
    }
    
    public void setMarkAll(boolean markAll) {
        this.markAll = markAll;
    }
    
    public Integer getUserId() {
        return userId;
    }
    
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
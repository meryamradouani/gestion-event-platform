package com.gestionevent.backend.dto.response;

public class UnreadCountResponse {
    private Integer userId;
    private Integer unreadCount;
    
    public UnreadCountResponse() {}
    
    public UnreadCountResponse(Integer userId, Integer unreadCount) {
        this.userId = userId;
        this.unreadCount = unreadCount;
    }
    
    public Integer getUserId() {
        return userId;
    }
    
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    
    public Integer getUnreadCount() {
        return unreadCount;
    }
    
    public void setUnreadCount(Integer unreadCount) {
        this.unreadCount = unreadCount;
    }
}
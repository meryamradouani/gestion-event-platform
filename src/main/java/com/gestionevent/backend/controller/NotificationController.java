package com.gestionevent.backend.controller;

import com.gestionevent.backend.dto.request.MarkAsReadRequest;
import com.gestionevent.backend.dto.response.ApiResponse;
import com.gestionevent.backend.dto.response.NotificationResponse;
import com.gestionevent.backend.dto.response.UnreadCountResponse;
import com.gestionevent.backend.service.NotificationQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    
    @Autowired
    private NotificationQueryService queryService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getNotifications(
            @RequestParam Integer userId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        
        Page<NotificationResponse> notifications = queryService.getUserNotifications(userId, pageable);
        ApiResponse<Page<NotificationResponse>> response = ApiResponse
                .success("Notifications récupérées", notifications);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/unread")
    public ResponseEntity<ApiResponse<?>> getUnreadNotifications(@RequestParam Integer userId) {
        var notifications = queryService.getUnreadNotifications(userId);
        ApiResponse<?> response = ApiResponse.success("Notifications non lues récupérées", notifications);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/unread/count")
    public ResponseEntity<ApiResponse<UnreadCountResponse>> getUnreadCount(@RequestParam Integer userId) {
        UnreadCountResponse count = queryService.getUnreadCount(userId);
        ApiResponse<UnreadCountResponse> response = ApiResponse.success("Compteur récupéré", count);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@RequestBody MarkAsReadRequest request) {
        if (request.isMarkAll() && request.getUserId() != null) {
            queryService.markAllAsRead(request.getUserId());
            ApiResponse<Void> response = ApiResponse.success("Toutes les notifications marquées comme lues");
            return ResponseEntity.ok(response);
        } else if (request.getNotificationId() != null) {
            queryService.markAsRead(request.getNotificationId());
            ApiResponse<Void> response = ApiResponse.success("Notification marquée comme lue");
            return ResponseEntity.ok(response);
        } else {
            ApiResponse<Void> response = ApiResponse.error("Paramètres invalides");
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PutMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(@RequestParam Integer userId) {
        queryService.markAllAsRead(userId);
        ApiResponse<Void> response = ApiResponse.success("Toutes les notifications marquées comme lues");
        return ResponseEntity.ok(response);
    }
}
package com.dlms.controller;

import com.dlms.model.Notification;
import com.dlms.model.User;
import com.dlms.repository.NotificationRepository;
import com.dlms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/applicant/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    // 1. Get all notifications for the logged-in user
    @GetMapping
    public ResponseEntity<?> getMyNotifications(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
            .orElseThrow(() -> new RuntimeException("User not found"));
            
        // Fetch notifications from newest to oldest
        List<Notification> notifications = notificationRepository.findByUserOrderBySentAtDesc(user);
        return ResponseEntity.ok(Map.of("data", notifications));
    }

    // 2. Mark a single notification as read (when user clicks it)
    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        notificationRepository.findById(id).ifPresent(notif -> {
            notif.setIsRead(true);
            notificationRepository.save(notif);
        });
        return ResponseEntity.ok(Map.of("success", true));
    }

    // 3. Mark all notifications as read (when user clicks "Mark all read")
    @PutMapping("/read-all")
    public ResponseEntity<?> markAllAsRead(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
            .orElseThrow(() -> new RuntimeException("User not found"));
            
        // Find only the unread ones and update them
        List<Notification> unread = notificationRepository.findByUserAndIsReadFalse(user);
        unread.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(unread);
        
        return ResponseEntity.ok(Map.of("success", true));
    }
}
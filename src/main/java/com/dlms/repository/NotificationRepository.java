package com.dlms.repository;

import com.dlms.model.Notification;
import com.dlms.model.User; // Make sure to import User!
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    // Gets all notifications for a user, newest first
    List<Notification> findByUserOrderBySentAtDesc(User user);
    
    // Gets only the unread notifications for the "Mark all as read" button
    List<Notification> findByUserAndIsReadFalse(User user);
    
    List<Notification> findByUser_UserIdOrderBySentAtDesc(Long userId);
    
    List<Notification> findByUser_UserIdAndIsRead(Long userId, Boolean isRead);
    
    List<Notification> findByUser_UserId(Long userId);
    
    long countByUser_UserIdAndIsRead(Long userId, Boolean isRead);
}
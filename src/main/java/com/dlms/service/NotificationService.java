package com.dlms.service;

import com.dlms.model.Notification;
import com.dlms.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * SERVICE LAYER — NotificationService
 * Retrieves and manages notifications for the user's bell icon / inbox.
 */
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final JdbcTemplate jdbcTemplate;

    /** Get all notifications for a user, newest first */
    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByUser_UserIdOrderBySentAtDesc(userId);
    }

    /** Count unread notifications (for the badge on the bell icon) */
    public long countUnread(Long userId) {
        return notificationRepository.countByUser_UserIdAndIsRead(userId, false);
    }

    /** Mark a specific notification as read */
    @Transactional
    public void markAsRead(Long notificationId) {
        jdbcTemplate.update("CALL SP_MARK_NOTIF_READ(?)", notificationId);
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        jdbcTemplate.update("CALL SP_MARK_ALL_NOTIF_READ(?)", userId);
    }
}

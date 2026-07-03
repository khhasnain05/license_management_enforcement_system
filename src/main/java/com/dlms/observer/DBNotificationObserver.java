package com.dlms.observer;

import com.dlms.model.Notification;
import com.dlms.model.User;
import com.dlms.repository.NotificationRepository;
import com.dlms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

/**
 * =============================================
 * DESIGN PATTERN: OBSERVER PATTERN — CONCRETE OBSERVER
 * =============================================
 * DBNotificationObserver saves every notification to the Oracle database.
 * Users can view their notifications in the portal.
 */
@Component
@RequiredArgsConstructor
public class DBNotificationObserver implements NotificationObserver {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Override
    public void update(Long userId, String type, String subject, String message) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return;

        Notification notification = Notification.builder()
                .user(user)
                .type(type)
                .subject(subject)
                .messageBody(message)
                .sentAt(LocalDateTime.now())
                .isRead(false)
                .build();

        notificationRepository.save(notification);
        System.out.println("[DB-NOTIF] Saved notification for user " + userId + ": " + subject);
    }
}

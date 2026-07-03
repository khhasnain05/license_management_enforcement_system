package com.dlms.observer;

import org.springframework.stereotype.Component;

/**
 * EmailNotificationObserver — sends email to the user.
 * In production, wire this to JavaMail / SendGrid / AWS SES.
 * For now it prints to the console (easy to test and extend).
 */
@Component
public class EmailNotificationObserver implements NotificationObserver {

    @Override
    public void update(Long userId, String type, String subject, String message) {
        // TODO: Integrate with JavaMailSender for real emails
        // Example: mailSender.send(buildEmail(userId, subject, message));
        System.out.println("[EMAIL-NOTIF] To user " + userId
                + " | Subject: " + subject
                + " | Body: " + message);
    }
}

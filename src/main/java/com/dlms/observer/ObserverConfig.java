package com.dlms.observer;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * =============================================
 * DESIGN PATTERN: OBSERVER PATTERN — WIRING
 * =============================================
 * This config class runs at application startup (CommandLineRunner)
 * and registers all observers with the NotificationSubject.
 *
 * To add a new notification channel (e.g., Push Notification):
 *   1. Create PushNotificationObserver implements NotificationObserver
 *   2. Add it to this config — zero changes to existing code!
 *      This demonstrates the Open/Closed Principle.
 */
@Component
@RequiredArgsConstructor
public class ObserverConfig implements CommandLineRunner {

    private final NotificationSubject subject;
    private final DBNotificationObserver dbObserver;
    private final EmailNotificationObserver emailObserver;

    @Override
    public void run(String... args) {
        // Register all observers at startup
        subject.addObserver(dbObserver);    // Always save to DB
        subject.addObserver(emailObserver); // Also send email

        System.out.println("[DLMS] Notification observers registered: DB + Email");
    }
}

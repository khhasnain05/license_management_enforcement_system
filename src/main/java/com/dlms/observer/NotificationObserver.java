package com.dlms.observer;

/**
 * =============================================
 * DESIGN PATTERN: OBSERVER PATTERN
 * =============================================
 * Purpose: When an important event happens (test result, license approved,
 * violation recorded), all registered observers are notified automatically.
 *
 * Components:
 *   NotificationObserver  - the interface every observer must implement
 *   NotificationSubject   - the subject that holds observers and fires events
 *   EmailNotificationObserver - sends email notifications
 *   SMSNotificationObserver   - sends SMS notifications (stub)
 *   DBNotificationObserver    - saves notification to the database
 *
 * Events that trigger notifications:
 *   - Theory test result (PASS/FAIL)
 *   - Practical test result
 *   - Medical certificate approved/rejected
 *   - License application approved/rejected
 *   - Traffic violation recorded
 */
public interface NotificationObserver {

    /**
     * Called by the subject when a notification event occurs.
     *
     * @param userId  The user to notify
     * @param type    Event type e.g. "TEST_RESULT", "LICENSE_APPROVED"
     * @param subject Short heading for the notification
     * @param message Detailed message body
     */
    void update(Long userId, String type, String subject, String message);
}

package com.dlms.observer;

import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

/**
 * =============================================
 * DESIGN PATTERN: OBSERVER PATTERN — SUBJECT
 * =============================================
 * The Subject (also called Publisher) maintains a list of observers
 * and notifies all of them when an event occurs.
 *
 * This is a Spring @Component (singleton bean) so it lives for the
 * lifetime of the application and observers register once at startup.
 */
@Component
public class NotificationSubject {

    // List of all registered observers
    private final List<NotificationObserver> observers = new ArrayList<>();

    /** Register a new observer (called at startup by Spring) */
    public void addObserver(NotificationObserver observer) {
        observers.add(observer);
    }

    /** Remove an observer if no longer needed */
    public void removeObserver(NotificationObserver observer) {
        observers.remove(observer);
    }

    /**
     * Broadcast an event to ALL registered observers.
     * Each observer decides independently how to handle it.
     *
     * Example:
     *   notificationSubject.notifyObservers(userId, "TEST_RESULT",
     *       "Theory Test Result", "You passed with 85%! Next: schedule practical test.");
     */
    public void notifyObservers(Long userId, String type, String subject, String message) {
        for (NotificationObserver observer : observers) {
            observer.update(userId, type, subject, message);
        }
    }
}

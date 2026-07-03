package com.dlms.model;

import jakarta.persistence.*;

import lombok.*;
import java.time.LocalDateTime;

/**
 * Notification entity — messages sent to users about system events.
 * Created by the Observer pattern when important events occur.
 */
@Entity
@Table(name = "NOTIFICATION")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notif_seq")
    @SequenceGenerator(name = "notif_seq", sequenceName = "NOTIF_SEQ", allocationSize = 1)
    @Column(name = "NOTIFICATION_ID")
    private Long notificationId;

    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    @Column(name = "TYPE", length = 30)
    private String type;                 // "TEST_RESULT", "LICENSE_APPROVED", "VIOLATION"

    @Column(name = "SUBJECT")
    private String subject;

    @Column(name = "MESSAGE_BODY", length = 1000)
    private String messageBody;

    @Column(name = "SENT_AT")
    private LocalDateTime sentAt;

    @Column(name = "IS_READ")
    @Builder.Default
    private Boolean isRead = false;
}

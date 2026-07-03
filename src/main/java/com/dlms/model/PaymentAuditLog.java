package com.dlms.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "PAYMENT_AUDIT_LOG")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentAuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long auditId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PAYMENT_ID", nullable = false)
    private Payment payment;

    @Column(name = "ACTION_TAKEN", nullable = false)
    private String actionTaken; // "REJECTED" or "APPROVED"

    @Column(name = "REMARKS", length = 1000)
    private String remarks; // The exact reason provided by the officer

    @Column(name = "OFFICER_ID", nullable = false)
    private Long officerId;

    @Column(name = "TIMESTAMP", nullable = false)
    private LocalDateTime timestamp;
}
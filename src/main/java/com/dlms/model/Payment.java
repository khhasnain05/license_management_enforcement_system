package com.dlms.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "PAYMENT")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pay_seq")
    @SequenceGenerator(name = "pay_seq", sequenceName = "PAYMENT_SEQ", allocationSize = 1)
    @Column(name = "PAYMENT_ID")
    private Long paymentId;

    // Link to the user who made the payment
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    // Link to the specific application
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "APPLICATION_ID", nullable = false)
    private Application application;

    @Column(name = "AMOUNT", nullable = false)
    private Double amount; // Will be 300.00 for Learner

    @Column(name = "PAYMENT_METHOD")
    private String paymentMethod; // 'JAZZCASH', 'EASYPAISA', 'BANK'

    @Column(name = "PAYMENT_STATUS")
    private String paymentStatus; // 'PENDING', 'COMPLETED', 'FAILED'

    @Column(name = "PAYMENT_TYPE")
    private String paymentType; // 'LEARNER_FEE', 'RENEWAL_FEE'

    @Column(name = "TRANSACTION_ID")
    private String transactionId;

    @Column(name = "PAYMENT_DATE")
    private LocalDateTime paymentDate;
    
    @Column(name = "CHALLAN_NO", unique = true, updatable = false)
    private String challanNo;
    
    private Long reviewedByOfficerId; // Stores the ID of the officer who verified/rejected
    
    @Column(name = "REMARKS")
    private String remarks; 
}
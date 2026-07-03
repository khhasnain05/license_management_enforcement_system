package com.dlms.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "LEARNER_PERMIT")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LearnerPermit {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "permit_seq")
    @SequenceGenerator(name = "permit_seq", sequenceName = "PERMIT_SEQ", allocationSize = 1)
    @Column(name = "PERMIT_ID")
    private Long permitId;
    
    @Column(name = "LEARNER_PERMIT_NO", unique = true, length = 50)
    private String learnerPermitNo;

    @OneToOne
    @JoinColumn(name = "APPLICATION_ID", nullable = false)
    private Application application;

    @Column(name = "ISSUE_DATE")
    private LocalDate issueDate;

    @Column(name = "EXPIRY_DATE")
    private LocalDate expiryDate;

    @Column(name = "STATUS", length = 20)
    private String status; // "ACTIVE", "EXPIRED"
}
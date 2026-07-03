package com.dlms.model;

import com.dlms.model.enums.ApplicationStage;
import com.dlms.model.enums.ApplicationStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Application entity — represents a license application submission.
 * An applicant can have multiple applications (e.g., new + renewal).
 * Central entity linking to payments, documents, tests, and the final license.
 */
@Entity
@Table(name = "APPLICATION")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Application {
    
    @Transient
    private String activeChallanNo;

    @Transient
    private String transactionId;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "app_seq")
    @SequenceGenerator(name = "app_seq", sequenceName = "APPLICATION_SEQ", allocationSize = 1)
    @Column(name = "APPLICATION_ID")
    private Long applicationId;

    @ManyToOne
    @JoinColumn(name = "APPLICANT_ID", nullable = false)
    private Applicant applicant;

    @Column(name = "LICENSE_CATEGORY", length = 10)
    private String licenseCategory;      // "A", "B", "C", etc.

    @Column(name = "APPLICATION_TYPE", length = 30)
    private String applicationType;      // "NEW", "RENEWAL", "DUPLICATE"

    // Keep the Enum version of the status
    @Enumerated(EnumType.STRING)
    @Column(name = "APPLICATION_STATUS")
    private ApplicationStatus applicationStatus;

    @Column(name = "SUBMISSION_DATE")
    private LocalDate submissionDate;

    @Column(name = "LAST_UPDATED_DATE")
    private LocalDateTime lastUpdatedDate;

    @Column(name = "REJECTION_REASON")
    private String rejectionReason;      // Filled if status = REJECTED

    @Enumerated(EnumType.STRING)
    @Column(name = "CURRENT_STAGE", length = 50)
    private ApplicationStage currentStage;

    // The officer reviewing this application (set when assigned)
    @ManyToOne
    @JoinColumn(name = "OFFICER_ID")
    private LicensingOfficer reviewingOfficer;
    
    private Integer theoryAttempts = 0;
    
    @Transient
    private String licenseNumber;

    @Transient
    private String learnerPermitNo;

    @Transient
    private LocalDate issueDate;

    @Transient
    private LocalDate learnerExpiryDate;
}
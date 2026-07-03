package com.dlms.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "MEDICAL_CERTIFICATE")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class MedicalCertificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CERTIFICATE_ID")
    private Long certificateId;

    @ManyToOne
    @JoinColumn(name = "APPLICATION_ID", nullable = false)
    private Application application;

    @ManyToOne
    @JoinColumn(name = "TEST_OFFICER_ID")
    private TestingOfficer reviewedBy;

    @Column(name = "STATUS", length = 50)
    private String status; // e.g., UNDER_REVIEW, APPROVED, REJECTED

    @Column(name = "APPROVAL_DATE")
    private LocalDateTime approvalDate;

    @Column(name = "REJECTION_REASON", length = 500)
    private String rejectionReason;

    @Column(name = "FILE_PATH", length = 500, nullable = false)
    private String filePath;
}
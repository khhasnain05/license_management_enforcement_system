package com.dlms.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

/**
 * DrivingLicense entity — the issued physical/digital license.
 * Created only after all requirements are met and application approved.
 */
@Entity
@Table(name = "DRIVING_LICENSE")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class DrivingLicense {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "license_seq")
    @SequenceGenerator(name = "license_seq", sequenceName = "LICENSE_SEQ", allocationSize = 1)
    @Column(name = "LICENSE_ID")
    private Long licenseId;
    
    @Column(name = "LICENSE_NUMBER", unique = true, length = 50)
    private String licenseNumber;

    @OneToOne
    @JoinColumn(name = "APPLICATION_ID", nullable = false)
    private Application application;

    @ManyToOne
    @JoinColumn(name = "APPLICANT_ID", nullable = false)
    private Applicant applicant;

    @Column(name = "CATEGORY", length = 20)
    private String category;

    @ManyToOne
    @JoinColumn(name = "OFFICER_ID")
    private LicensingOfficer issuingOfficer;

    @Column(name = "ISSUE_DATE")
    private LocalDate issueDate;

    @Column(name = "EXPIRY_DATE")
    private LocalDate expiryDate;

    @Column(name = "STATUS", length = 20)
    private String status;               // "ACTIVE", "EXPIRED", "SUSPENDED", "REVOKED"

    @Column(name = "QR_CODE_DATA", length = 500)
    private String qrCodeData;           // Encoded data for QR scanning by police
    
    @Column(name = "TOTAL_PENALTY_POINTS")
    private Integer totalPenaltyPoints = 0;

    @Column(name = "SUSPENSION_END_DATE")
    private LocalDate suspensionEndDate;
}
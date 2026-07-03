package com.dlms.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * TrafficViolation — recorded by traffic police against a license.
 */
@Entity
@Table(name = "TRAFFIC_VIOLATION")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class TrafficViolation {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "violation_seq")
    @SequenceGenerator(name = "violation_seq", sequenceName = "VIOLATION_SEQ", allocationSize = 1)
    @Column(name = "VIOLATION_ID")
    private Long violationId;

    @ManyToOne
    @JoinColumn(name = "LICENSE_ID", nullable = false)
    private DrivingLicense license;
    
    @ManyToOne
    @JoinColumn(name = "POLICE_OFFICER_ID")
    private TrafficPoliceOfficer policeOfficer;

    @Column(name = "CHALLAN_NO", unique = true)
    private String challanNo;

    @Column(name = "VIOLATION_TYPE")
    private String violationType;        // e.g., "SPEEDING", "SIGNAL_JUMP"

    @Column(name = "FINE_AMOUNT", precision = 10, scale = 2)
    private BigDecimal fineAmount;

    @Column(name = "PENALTY_POINTS")
    private Integer penaltyPoints;

    @Column(name = "VIOLATION_DATE")
    private LocalDate violationDate;

    @Column(name = "VIOLATION_LOCATION")
    private String violationLocation;

    @Column(name = "VEHICLE_REG_NO")
    private String vehicleRegNo;

    @Column(name = "CHALLAN_STATUS", length = 20)
    private String challanStatus;        // "PENDING", "PAID", "DISPUTED"
}

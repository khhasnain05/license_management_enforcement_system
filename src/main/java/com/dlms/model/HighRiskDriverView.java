package com.dlms.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.Immutable;
import java.time.LocalDate;

@Entity
@Immutable
@Table(name = "VW_HIGH_RISK_DRIVERS")
@Data
public class HighRiskDriverView {

    @Id // License number is guaranteed to be unique
    @Column(name = "LICENSE_NUMBER")
    private String licenseNumber;

    @Column(name = "FULL_NAME_EN")
    private String fullNameEn;

    @Column(name = "CNIC")
    private String cnic;

    @Column(name = "MOBILE_NUMBER")
    private String mobileNumber;

    @Column(name = "TOTAL_PENALTY_POINTS")
    private Integer totalPenaltyPoints;

    @Column(name = "LICENSE_STATUS")
    private String licenseStatus;

    @Column(name = "SUSPENSION_END_DATE")
    private LocalDate suspensionEndDate;
}
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
@Table(name = "VW_APPLICANT_DASHBOARD")
@Data
public class ApplicantDashboardView {

    @Id 
    @Column(name = "APPLICATION_ID")
    private Long applicationId;

    @Column(name = "CNIC")
    private String cnic;

    @Column(name = "FULL_NAME_EN")
    private String fullNameEn;

    @Column(name = "DISTRICT")
    private String district;

    @Column(name = "LICENSE_CATEGORY")
    private String licenseCategory;

    @Column(name = "APPLICATION_TYPE")
    private String applicationType;

    @Column(name = "APPLICATION_STATUS")
    private String applicationStatus;

    @Column(name = "CURRENT_STAGE")
    private String currentStage;

    @Column(name = "LEARNER_PERMIT_NO")
    private String learnerPermitNo;

    @Column(name = "LEARNER_EXPIRY")
    private LocalDate learnerExpiry;

    @Column(name = "LICENSE_NUMBER")
    private String licenseNumber;

    @Column(name = "LICENSE_STATUS")
    private String licenseStatus;
}
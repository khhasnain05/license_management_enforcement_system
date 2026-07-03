package com.dlms.service;

import com.dlms.model.Application;
import com.dlms.model.DrivingLicense;
import com.dlms.model.LicensingOfficer;
import com.dlms.observer.NotificationSubject;
import com.dlms.repository.ApplicationRepository;
import com.dlms.repository.LicenseRepository;
import com.dlms.model.enums.ApplicationStage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LicenseService {

    private final LicenseRepository licenseRepository;
    private final ApplicationRepository applicationRepository;
    private final NotificationSubject notificationSubject;
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public DrivingLicense issueLicense(Application application, LicensingOfficer officer) {
        jdbcTemplate.update("CALL SP_ISSUE_LICENSE(?, ?)", 
            application.getApplicationId(), officer.getOfficerId());

        notificationSubject.notifyObservers(
                application.getApplicant().getUser().getUserId(),
                "LICENSE_ISSUED", "Permanent License Issued",
                "Your permanent driving license has been issued!"
        );
        // Fetch and return the newly generated license for the controller response
        return licenseRepository.findByApplication_ApplicationId(application.getApplicationId()).orElseThrow();
    }

    public Optional<DrivingLicense> findById(Long id) {
        return licenseRepository.findById(id);
    }

    public Optional<DrivingLicense> verifyByQrCode(String qrCodeData) {
        return licenseRepository.findByQrCodeData(qrCodeData);
    }

    public List<DrivingLicense> getLicensesByApplicant(Long applicantId) {
        return licenseRepository.findByApplication_Applicant_ApplicantId(applicantId);
    }

    @Transactional
    public DrivingLicense suspendLicense(Long licenseId, String reason) {
        DrivingLicense license = licenseRepository.findById(licenseId)
                .orElseThrow(() -> new RuntimeException("License not found: " + licenseId));
        license.setStatus("SUSPENDED");
        DrivingLicense saved = licenseRepository.save(license);

        notificationSubject.notifyObservers(
                license.getApplication().getApplicant().getUser().getUserId(),
                "LICENSE_SUSPENDED", "License Suspended",
                "Your license has been suspended. Reason: " + reason
        );
        return saved;
    }
}
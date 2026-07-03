package com.dlms.service;

import com.dlms.dto.PaymentRequest;
import com.dlms.model.*;
import com.dlms.model.enums.ApplicationStage;
import com.dlms.model.enums.ApplicationStatus;
import com.dlms.observer.NotificationSubject;
import com.dlms.repository.*;
import lombok.RequiredArgsConstructor;

import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final NotificationSubject notificationSubject;
    private final ApplicantRepository applicantRepository;
    private final LearnerPermitRepository learnerPermitRepository;
    private final DrivingLicenseRepository drivingLicenseRepository;
    private final LicenseStatusHistoryRepository licenseStatusHistoryRepository;
    private final PaymentRepository paymentRepository;
    private final NotificationRepository notificationRepository;
    private final LicenseService licenseService;
    private final PaymentService paymentService;
    private final LicensingOfficerRepository officerRepository;
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public Application createLearnerPermit(String userEmail, String category) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("SP_CREATE_LEARNER_PERMIT");

        Map<String, Object> inParams = Map.of(
                "p_applicant_user_email", userEmail,
                "p_category", category
        );

        Map<String, Object> out = jdbcCall.execute(inParams);
        Long newAppId = ((Number) out.get("P_APPLICATION_ID")).longValue();

        return applicationRepository.findById(newAppId).orElseThrow();
    }

    @Transactional
    public void submitFeeForVerification(Long appId, PaymentRequest req) {
        jdbcTemplate.update("CALL SP_UPDATE_PAYMENT_SUBMIT(?, ?, ?)",
            appId, req.getTransactionId(), req.getPaymentMethod()
        );
    }

    @Transactional
    public void resetApplicationForRenewal(Long applicationId, String userEmail) {
        Application app = applicationRepository.findById(applicationId).orElseThrow();
        app.setCurrentStage(ApplicationStage.LEARNER_PAYMENT_PENDING);
        app.setApplicationStatus(ApplicationStatus.PENDING);
        app.setLastUpdatedDate(LocalDateTime.now());
        applicationRepository.save(app);
    }

    @Transactional
    public Application advanceStage(Long applicationId, ApplicationStage newStage) {
        Application app = findByIdOrThrow(applicationId);
        app.setCurrentStage(newStage);
        app.setLastUpdatedDate(LocalDateTime.now());
        return applicationRepository.save(app);
    }

    public List<Application> getAllApplications() {
        List<Application> apps = applicationRepository.findAll();
        attachPaymentDataForOfficer(apps);
        return apps;
    }

    public List<Application> getApplicationsByStatus(ApplicationStatus status) {
        List<Application> apps = applicationRepository.findByApplicationStatus(status);
        attachPaymentDataForOfficer(apps);
        return apps;
    }

    public List<Application> getOfficerApplications(Long officerId) {
        List<Application> apps = applicationRepository.findByReviewingOfficer_OfficerId(officerId);
        attachPaymentDataForOfficer(apps);
        return apps;
    }

    private void attachPaymentDataForOfficer(List<Application> apps) {
        for (Application app : apps) {
            paymentRepository.findTopByApplicationOrderByPaymentDateDesc(app).ifPresent(payment -> {
                app.setActiveChallanNo(payment.getChallanNo());
                app.setTransactionId(payment.getTransactionId());
            });
        }
    }

    @Transactional
    public Application approveApplication(Long applicationId, LicensingOfficer officer) {
        Application app = findByIdOrThrow(applicationId);
        licenseService.issueLicense(app, officer);
        app.setApplicationStatus(ApplicationStatus.APPROVED);
        app.setCurrentStage(ApplicationStage.LICENSE_READY);
        return applicationRepository.save(app);
    }

    public Optional<Application> findById(Long id) { return applicationRepository.findById(id); }

    private Application findByIdOrThrow(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found: " + id));
    }
    
    public List<Application> getPendingPaymentApplications() {
        // Use the new Enum list!
        List<Application> pendingQueue = applicationRepository.findByCurrentStageIn(List.of(
            ApplicationStage.PAYMENT_VERIFICATION, 
            ApplicationStage.FINAL_PAYMENT_VERIFICATION, 
            ApplicationStage.FINAL_REVIEW
        ));
        
        attachPaymentDataForOfficer(pendingQueue);
        
        return pendingQueue;
    }
    
    public List<Application> getApplicationsByApplicant(Long applicantId) {
        List<Application> apps = applicationRepository.findByApplicant_ApplicantId(applicantId);
        
        for (Application app : apps) {
            // Attach Permit info so the frontend doesn't show "null" dates
            learnerPermitRepository.findByApplication_ApplicationId(app.getApplicationId())
                .ifPresent(permit -> {
                    app.setLearnerPermitNo(permit.getLearnerPermitNo());
                    app.setIssueDate(permit.getIssueDate());
                    app.setLearnerExpiryDate(permit.getExpiryDate());
                });

            // Attach Permanent License info (if they have one)
            if (app.getCurrentStage() == ApplicationStage.LICENSE_READY || app.getCurrentStage() == ApplicationStage.COMPLETED) {
                drivingLicenseRepository.findByApplication_ApplicationId(app.getApplicationId())
                    .ifPresent(license -> app.setLicenseNumber(license.getLicenseNumber()));
            }
        }
        return apps;
    }
}
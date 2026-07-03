package com.dlms.service;

import com.dlms.model.*;
import com.dlms.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OfficerService {

    private final TestingOfficerRepository officerRepository;
    private final MedicalCertificateRepository medicalCertificateRepository;
    private final DrivingTestScheduleRepository drivingTestScheduleRepository;
    private final PracticalTestResultRepository practicalTestResultRepository;
    private final ApplicationRepository applicationRepository;
    private final JdbcTemplate jdbcTemplate;
    
    private final PaymentRepository paymentRepository;
    private final NotificationRepository notificationRepository;

    private String getOfficerDistrict(Long officerId) {
        TestingOfficer officer = officerRepository.findById(officerId)
                .orElseThrow(() -> new RuntimeException("Testing Officer not found for ID: " + officerId));
        return officer.getUser().getDistrict();
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  MEDICAL CERTIFICATES  — district-scoped
    // ══════════════════════════════════════════════════════════════════════════

    public List<MedicalCertificate> getPendingMedicalCertificates(Long officerId) {
        String district = getOfficerDistrict(officerId);
        return medicalCertificateRepository
                .findByStatusAndApplication_Applicant_User_District("UNDER_REVIEW", district);
    }

    public List<MedicalCertificate> getReviewedMedicalCertificates(Long officerId) {
        String district = getOfficerDistrict(officerId);
        return medicalCertificateRepository
                .findByStatusNotAndApplication_Applicant_User_District("UNDER_REVIEW", district);
    }

    @Transactional
    public void reviewMedicalCertificate(Long certificateId, Long officerId, boolean isApproved, String comments) {
        String status = isApproved ? "APPROVED" : "REJECTED";
        String finalComments = (comments != null && !comments.isEmpty()) ? comments : "No specific reason provided.";

        jdbcTemplate.update("CALL SP_REVIEW_MEDICAL_CERT(?, ?, ?, ?)",
            certificateId, officerId, status, finalComments
        );
    }

    public List<DrivingTestSchedule> getPracticalTestsForOfficer(Long officerId) {
        TestingOfficer officer = officerRepository.findById(officerId)
                .orElseThrow(() -> new RuntimeException("Officer not found"));
        
        // Get the center the officer is actually in charge of
        String assignedCenter = officer.getAssignedCenter(); 

        return drivingTestScheduleRepository.findAll().stream()
                .filter(s -> "SCHEDULED".equals(s.getStatus()))
                .filter(s -> assignedCenter.equalsIgnoreCase(s.getTestCenter()))
                .collect(Collectors.toList());
    }

    public List<PracticalTestResult> getPracticalTestHistoryForOfficer(Long officerId) {
        TestingOfficer officer = officerRepository.findById(officerId)
                .orElseThrow(() -> new RuntimeException("Officer not found"));
        
        String assignedCenter = officer.getAssignedCenter();

        return practicalTestResultRepository.findAll().stream()
                .filter(result -> {
                    return drivingTestScheduleRepository.findByApplication_ApplicationId(
                            result.getApplication().getApplicationId())
                            .map(schedule -> assignedCenter.equalsIgnoreCase(schedule.getTestCenter()))
                            .orElse(false);
                })
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void evaluatePracticalTest(Map<String, Object> payload, Long officerId) {
        Long appId = Long.valueOf(payload.get("applicationId").toString());
        int parking = Integer.parseInt(payload.getOrDefault("parkingScore", "0").toString());
        int lane = Integer.parseInt(payload.getOrDefault("laneScore", "0").toString());
        int sign = Integer.parseInt(payload.getOrDefault("signScore", "0").toString());
        int total = Integer.parseInt(payload.getOrDefault("totalScore", "0").toString());

        jdbcTemplate.update("CALL SP_EVALUATE_PRACTICAL_TEST(?, ?, ?, ?, ?, ?)",
                appId, officerId, parking, lane, sign, total);
    }
}
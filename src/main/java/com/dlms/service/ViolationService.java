package com.dlms.service;

import com.dlms.model.DrivingLicense;
import com.dlms.model.TrafficViolation;
import com.dlms.observer.NotificationSubject;
import com.dlms.repository.LicenseRepository;
import com.dlms.repository.ViolationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * SERVICE LAYER — ViolationService
 * Records and manages traffic violations.
 * Uses Observer Pattern to notify the license holder.
 */
@Service
@RequiredArgsConstructor
public class ViolationService {

    private final ViolationRepository violationRepository;
    private final LicenseRepository licenseRepository;
    private final NotificationSubject notificationSubject;
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void issueChallan(Map<String, Object> payload, Long officerId) {
        Long licenseId = Long.valueOf(payload.get("licenseId").toString());
        Integer points = Integer.valueOf(payload.get("penaltyPoints").toString());
        Double fine = Double.valueOf(payload.get("fineAmount").toString());

        jdbcTemplate.update("CALL SP_RECORD_VIOLATION(?, ?, ?, ?, ?, ?, ?, ?)",
            licenseId, officerId, payload.get("challanNo").toString(), 
            payload.get("vehicleRegNo").toString(), payload.get("violationType").toString(),
            fine, points, payload.get("violationLocation").toString()
        );
    }

    public List<TrafficViolation> getByLicense(Long licenseId) {
        return violationRepository.findByLicense_LicenseId(licenseId);
    }

    public List<TrafficViolation> getViolationsByLicense(Long licenseId) {
        return violationRepository.findByLicense_LicenseId(licenseId);
    }

    @Transactional
    public TrafficViolation markChallanPaid(Long violationId) {
        TrafficViolation v = violationRepository.findById(violationId)
                .orElseThrow(() -> new RuntimeException("Violation not found: " + violationId));
        v.setChallanStatus("PAID");
        return violationRepository.save(v);
    }

    @Transactional
    public TrafficViolation markAsPaid(Long violationId) {
        return markChallanPaid(violationId);
    }
}

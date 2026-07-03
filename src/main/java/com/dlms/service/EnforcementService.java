package com.dlms.service;

import com.dlms.model.*;
import com.dlms.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnforcementService {

    private final DrivingLicenseRepository drivingLicenseRepository;
    private final ViolationRepository violationRepository;
    private final TrafficPoliceOfficerRepository policeRepository;
    private final JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> getOfficerViolations(Long officerId) {
        List<TrafficViolation> violations = violationRepository.findByPoliceOfficer_PoliceOfficerIdOrderByViolationDateDesc(officerId);
        
        return violations.stream().map(v -> {
            Map<String, Object> map = new HashMap<>();
            map.put("violationId", v.getViolationId());
            map.put("challanNo", v.getChallanNo());
            map.put("licenseId", v.getLicense().getLicenseId());
            map.put("driverName", v.getLicense().getApplication().getApplicant().getUser().getFullNameEn());
            map.put("vehicleRegNo", v.getVehicleRegNo());
            map.put("violationType", v.getViolationType());
            map.put("fineAmount", v.getFineAmount());
            map.put("penaltyPoints", v.getPenaltyPoints());
            map.put("violationDate", v.getViolationDate());
            map.put("violationLocation", v.getViolationLocation());
            map.put("challanStatus", v.getChallanStatus());
            return map;
        }).collect(Collectors.toList());
    }

    @Transactional
    public Map<String, Object> verifyLicense(String query) {
        DrivingLicense license = drivingLicenseRepository.findByIdOrCnic(query)
                .orElseThrow(() -> new RuntimeException("License not found for ID/CNIC: " + query));

        jdbcTemplate.update("CALL SP_AUTO_UNBLOCK_LICENSE(?)", license.getLicenseId());

        List<TrafficViolation> pastViolations = violationRepository.findByLicense_LicenseId(license.getLicenseId());

        Map<String, Object> result = new HashMap<>();
        result.put("licenseId", license.getLicenseId());
        result.put("category", license.getApplication().getLicenseCategory());
        result.put("status", license.getStatus());
        result.put("issueDate", license.getIssueDate());
        result.put("expiryDate", license.getExpiryDate());
        result.put("holderName", license.getApplication().getApplicant().getUser().getFullNameEn());
        result.put("cnic", license.getApplication().getApplicant().getUser().getCnic());
        result.put("totalViolations", pastViolations.size());
        
        int currentPoints = license.getTotalPenaltyPoints() == null ? 0 : license.getTotalPenaltyPoints();
        result.put("totalPenaltyPoints", currentPoints);
        result.put("suspensionEndDate", license.getSuspensionEndDate());

        return result;
    }

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

    @Transactional
    public void markChallanPaid(Long violationId) {
        TrafficViolation violation = violationRepository.findById(violationId)
                .orElseThrow(() -> new RuntimeException("Violation not found"));
        
        if ("PAID".equals(violation.getChallanStatus())) {
            throw new RuntimeException("Challan is already marked as paid.");
        }
        violation.setChallanStatus("PAID");
        violationRepository.save(violation);
    }

    public List<Map<String, Object>> getViolationsByCnic(String cnic) {
        DrivingLicense license = drivingLicenseRepository.findByIdOrCnic(cnic).orElse(null);
        if (license == null) return List.of();

        return violationRepository.findByLicense_LicenseId(license.getLicenseId())
                .stream().map(v -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("violationId", v.getViolationId());
                    map.put("challanNo", v.getChallanNo());
                    map.put("violationType", v.getViolationType());
                    map.put("fineAmount", v.getFineAmount());
                    map.put("penaltyPoints", v.getPenaltyPoints());
                    map.put("violationDate", v.getViolationDate());
                    map.put("challanStatus", v.getChallanStatus());
                    return map;
                }).collect(Collectors.toList());
    }
}
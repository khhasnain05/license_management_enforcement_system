package com.dlms.controller;

import com.dlms.service.ApplicationService;
import com.dlms.dto.PaymentRequest; // Make sure to import your new DTO
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController 
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @Data
    public static class LearnerRequest {
        private String licenseCategory;
        private String applicationType;
    }

    // 1. APPLY: Creates application with stage 'LEARNER_PAYMENT_PENDING'
    @PostMapping("/apply")
    public ResponseEntity<?> applyForLearner(@RequestBody LearnerRequest request, 
                                             Authentication authentication) {
        try {
            applicationService.createLearnerPermit(authentication.getName(), request.getLicenseCategory());
            return ResponseEntity.ok(Map.of(
                "success", true, 
                "message", "Application submitted! Please pay Rs. 300 to proceed."
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 2. SUBMIT FEE: New method to handle Rs. 300 submission for Officer Review
    // This replaces the old auto-verify method
    @PostMapping("/{id}/submit-fee")
    public ResponseEntity<?> submitLearnerFee(@PathVariable Long id, 
                                              @RequestBody PaymentRequest request) {
        try {
            // Logic: 1. Save payment as 'PENDING'.
            // 2. Set application stage to 'PAYMENT_VERIFICATION' (Locks UI).
            // 3. Officer will approve this later.
            applicationService.submitFeeForVerification(id, request);
            
            return ResponseEntity.ok(Map.of(
                "success", true, 
                "message", "Payment submitted! Waiting for Licensing Officer to verify."
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 3. RENEW: Resets existing application for a new payment cycle
    @PutMapping("/{id}/renew-learner")
    public ResponseEntity<?> renewExistingLearner(@PathVariable Long id, Authentication authentication) {
        try {
            applicationService.resetApplicationForRenewal(id, authentication.getName());
            return ResponseEntity.ok(Map.of(
                "success", true, 
                "message", "Renewal initiated. Please pay Rs. 300 fee."
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

 // 4. REQUEST FINAL LICENSE: Moves application to FINAL_REVIEW stage
    @PostMapping("/{id}/request-final-license")
    public ResponseEntity<?> requestFinalLicense(@PathVariable Long id) {
        try {
            applicationService.advanceStage(id, com.dlms.model.enums.ApplicationStage.FINAL_REVIEW);
            
            return ResponseEntity.ok(Map.of(
                "success", true, 
                "message", "Sent to Licensing Officer for final validation."
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
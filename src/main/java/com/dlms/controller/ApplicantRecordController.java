package com.dlms.controller;

import com.dlms.service.EnforcementService;
import com.dlms.repository.UserRepository;
import com.dlms.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/applicant")
@RequiredArgsConstructor
public class ApplicantRecordController {

    private final EnforcementService enforcementService;
    private final UserRepository userRepository;

    @GetMapping("/my-driving-record")
    public ResponseEntity<?> getMyDrivingRecord(Principal principal) {
        try {
            User user = userRepository.findByEmail(principal.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            Map<String, Object> licenseData = enforcementService.verifyLicense(user.getCnic());
            List<Map<String, Object>> violations = enforcementService.getViolationsByCnic(user.getCnic());
            
            return ResponseEntity.ok(Map.of(
                "license", licenseData,
                "violations", violations
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
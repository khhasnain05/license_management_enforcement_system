package com.dlms.controller;

import com.dlms.model.TrafficPoliceOfficer;
import com.dlms.repository.TrafficPoliceOfficerRepository;
import com.dlms.service.EnforcementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/enforcement")
@RequiredArgsConstructor
public class EnforcementController {

    private final EnforcementService enforcementService;
    private final TrafficPoliceOfficerRepository policeRepository;

    private TrafficPoliceOfficer getLoggedInOfficer(Principal principal) {
        return policeRepository.findByUser_Email(principal.getName())
                .orElseThrow(() -> new RuntimeException("Police Officer not found"));
    }

    @GetMapping("/my-violations")
    public ResponseEntity<?> getMyViolations(Principal principal) {
        try {
            TrafficPoliceOfficer officer = getLoggedInOfficer(principal);
            return ResponseEntity.ok(enforcementService.getOfficerViolations(officer.getPoliceOfficerId()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/verify/{query}")
    public ResponseEntity<?> verifyLicense(@PathVariable String query) {
        try {
            return ResponseEntity.ok(enforcementService.verifyLicense(query));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/violations")
    public ResponseEntity<?> issueViolation(@RequestBody Map<String, Object> payload, Principal principal) {
        try {
            TrafficPoliceOfficer officer = getLoggedInOfficer(principal);
            enforcementService.issueChallan(payload, officer.getPoliceOfficerId());
            return ResponseEntity.ok(Map.of("success", true, "message", "Challan Issued"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PutMapping("/violations/{id}/paid")
    public ResponseEntity<?> markPaid(@PathVariable Long id) {
        try {
            enforcementService.markChallanPaid(id);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
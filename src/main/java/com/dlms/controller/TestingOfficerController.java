package com.dlms.controller;

import com.dlms.model.DrivingTestSchedule;
import com.dlms.model.MedicalCertificate;
import com.dlms.model.TestingOfficer;
import com.dlms.model.User;
import com.dlms.repository.TestingOfficerRepository;
import com.dlms.repository.UserRepository;
import com.dlms.service.OfficerService;
import com.dlms.service.TestService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/officer")
@RequiredArgsConstructor
public class TestingOfficerController {

    private final OfficerService officerService;
    private final TestService testService;
    private final TestingOfficerRepository testingOfficerRepository;
    private final UserRepository userRepository;

    private TestingOfficer getLoggedInOfficer(Principal principal) {
        return testingOfficerRepository.findByUser_Email(principal.getName())
                .orElseThrow(() -> new RuntimeException("Testing Officer profile not found!"));
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  MEDICAL CERTIFICATES
    // ══════════════════════════════════════════════════════════════════════════

    @GetMapping("/medical/pending")
    public ResponseEntity<List<MedicalCertificate>> getPendingMedical(Principal principal) {
        TestingOfficer officer = getLoggedInOfficer(principal);
        return ResponseEntity.ok(
                officerService.getPendingMedicalCertificates(officer.getOfficerId()));
    }

    @GetMapping("/medical/history")
    public ResponseEntity<List<MedicalCertificate>> getMedicalHistory(Principal principal) {
        TestingOfficer officer = getLoggedInOfficer(principal);
        return ResponseEntity.ok(
                officerService.getReviewedMedicalCertificates(officer.getOfficerId()));
    }

    @PostMapping("/medical/review")
    public ResponseEntity<?> reviewCertificate(
            @RequestParam("certificateId") Long certificateId,
            @RequestParam("isApproved")    boolean isApproved,
            @RequestParam(value = "comments", required = false) String comments,
            Principal principal) {

        try {
            TestingOfficer officer = getLoggedInOfficer(principal);
            officerService.reviewMedicalCertificate(
                    certificateId, officer.getOfficerId(), isApproved, comments);
            return ResponseEntity.ok(Map.of("success", true, "message", "Review submitted."));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  PRACTICAL TESTS
    // ══════════════════════════════════════════════════════════════════════════

    @GetMapping("/practical-tests/pending")
    public ResponseEntity<List<DrivingTestSchedule>> getPendingPracticalTests(Principal principal) {
        TestingOfficer officer = getLoggedInOfficer(principal);
        return ResponseEntity.ok(
                officerService.getPracticalTestsForOfficer(officer.getOfficerId()));
    }

    @GetMapping("/practical-tests/history")
    public ResponseEntity<?> getPracticalTestHistory(Principal principal) {
        try {
            TestingOfficer officer = getLoggedInOfficer(principal);
            // The <?> allows Spring to accept the List<PracticalTestResult> safely
            return ResponseEntity.ok(officerService.getPracticalTestHistoryForOfficer(officer.getOfficerId()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/practical-tests/evaluate")
    public ResponseEntity<?> evaluatePracticalTest(
            @RequestBody Map<String, Object> payload,
            Principal principal) {
        try {
            TestingOfficer officer = getLoggedInOfficer(principal);
            officerService.evaluatePracticalTest(payload, officer.getOfficerId());
            return ResponseEntity.ok(Map.of("success", true, "message", "Result saved."));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    @GetMapping("/practical-tests/slots")
    public ResponseEntity<?> getAvailableSlots(
            @RequestParam("testCenter") String testCenter,
            @RequestParam("date") String dateStr) {
        try {
            LocalDate date = LocalDate.parse(dateStr);
            long remaining = testService.getRemainingSlots(testCenter, date);
            
            return ResponseEntity.ok(Map.of(
                "remainingSlots", remaining,
                "isAvailable", remaining > 0
            ));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/api/testing-officer/me")
    public ResponseEntity<?> getCurrentOfficer(Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                      .orElseThrow(() -> new RuntimeException("User not found"));
        
        TestingOfficer officer = testingOfficerRepository.findByUser(user)
                      .orElseThrow(() -> new RuntimeException("Officer profile not found"));
        
        Map<String, Object> response = new HashMap<>();
        response.put("officerId", officer.getOfficerId());
        response.put("assignedCenter", officer.getAssignedCenter());
        response.put("district", user.getDistrict()); // from USERS table
        response.put("fullNameEn", user.getFullNameEn());
        response.put("email", user.getEmail());
        
        return ResponseEntity.ok(response);
    }
}
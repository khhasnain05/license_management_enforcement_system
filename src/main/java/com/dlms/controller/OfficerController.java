package com.dlms.controller;

import com.dlms.dto.ApiResponse;
import com.dlms.model.Application;
import com.dlms.model.LicensingOfficer;
import com.dlms.repository.ApplicationRepository;
import com.dlms.repository.LicensingOfficerRepository;
import com.dlms.service.ApplicationService;
import com.dlms.service.ApplicationWorkflowService;
import com.dlms.service.PaymentService;
import com.dlms.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/officer/applications") // Unified base path
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('LICENSING_OFFICER', 'TESTING_OFFICER')")
public class OfficerController {

    private final ApplicationService applicationService;
    private final TestService testService;
    private final LicensingOfficerRepository officerRepository;
    private final ApplicationRepository applicationRepository;
    private final ApplicationWorkflowService workflowService;
    private final PaymentService paymentService;

    @GetMapping("/pending-payment")
    public ResponseEntity<?> getPendingPayments() {
        return ResponseEntity.ok(Map.of("data", applicationService.getPendingPaymentApplications()));
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllApplications() {
        return ResponseEntity.ok(Map.of("data", applicationService.getAllApplications()));
    }

    @PutMapping("/{id}/verify-payment")
    public ResponseEntity<?> verifyPayment(@PathVariable Long id, Principal principal) {
        LicensingOfficer officer = getOfficerByEmail(principal.getName());
        
        workflowService.verifyPayment(id, officer.getOfficerId());
        return ResponseEntity.ok(Map.of("success", true, "message", "Payment verified successfully"));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectApplication(@PathVariable Long id, @RequestBody Map<String, String> body, Principal principal) {
        LicensingOfficer officer = getOfficerByEmail(principal.getName());
        String reason = body.getOrDefault("reason", "Payment receipt was invalid or unreadable.");
        
        workflowService.rejectPayment(id, officer.getOfficerId(), reason);
        return ResponseEntity.ok(Map.of("success", true, "message", "Payment rejected. Applicant notified to try again."));
    }

    @PutMapping("/{id}/verify-final-fee")
    public ResponseEntity<?> verifyFinalFee(@PathVariable Long id, Principal principal) {
        LicensingOfficer officer = getOfficerByEmail(principal.getName());
        workflowService.verifyPayment(id, officer.getOfficerId()); // Reuses the exact same logic!
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/{id}/schedule-test")
    public ResponseEntity<?> scheduleTest(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        
        Application app = applicationService.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found: " + id));

        String testType = body.get("testType");
        String testCenter = body.get("testCenter");
        LocalDate testDate = LocalDate.parse(body.get("testDate"));

        testService.scheduleTest(app, testType, testCenter, testDate);
        
        return ResponseEntity.ok(Map.of("success", true, "message", "Test scheduled successfully in Oracle DB"));
    }

    private LicensingOfficer getOfficerByEmail(String email) {
        return officerRepository.findByUser_Email(email)
                .orElseThrow(() -> new RuntimeException("Officer not found: " + email));
    }

    @PostMapping("/{id}/issue-license")
    public ResponseEntity<?> issuePermanentLicense(@PathVariable Long id, Principal principal) {
        LicensingOfficer officer = getOfficerByEmail(principal.getName());
        applicationService.approveApplication(id, officer); 
        return ResponseEntity.ok(Map.of("success", true));
    }
    
    @GetMapping("/my-history")
    public ResponseEntity<?> getMyHistory(Principal principal) {
        LicensingOfficer officer = getOfficerByEmail(principal.getName());
        return ResponseEntity.ok(Map.of("data", paymentService.getOfficerAuditHistory(officer.getOfficerId())));
    }

    @GetMapping("/verify/{query}")
    public ResponseEntity<?> verifyLicenseSearch(@PathVariable String query) {
        for (Application app : applicationRepository.findAll()) {
            if (app.getApplicant().getUser().getCnic().equals(query) || 
               (app.getLicenseNumber() != null && app.getLicenseNumber().equals(query))) {
                return ResponseEntity.ok(Map.of("success", true, "data", app));
            }
        }
        return ResponseEntity.ok(Map.of("success", false));
    }
}
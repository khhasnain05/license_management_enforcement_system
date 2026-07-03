package com.dlms.controller;

import com.dlms.model.Applicant;
import com.dlms.model.Application;
import com.dlms.repository.ApplicantRepository;
import com.dlms.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController // Speaks JSON!
@RequestMapping("/api/applicant")
@RequiredArgsConstructor
public class ApplicantController {

    private final ApplicationService applicationService;
    private final ApplicantRepository applicantRepository;

    // 1. Send User Profile Data to the Sidebar
    @GetMapping("/me")
    public ResponseEntity<?> getMe(Authentication authentication) {
        Applicant applicant = applicantRepository.findByUser_Email(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Applicant not found"));
        
        // Build a clean, flat object so the frontend gets exactly what it needs without nesting
        Map<String, Object> userData = new HashMap<>();
        userData.put("applicantId", applicant.getApplicantId());
        
        if (applicant.getUser() != null) {
            userData.put("fullNameEn", applicant.getUser().getFullNameEn());
            userData.put("cnic", applicant.getUser().getCnic());
            userData.put("district", applicant.getUser().getDistrict());
            userData.put("email", applicant.getUser().getEmail());
        }

        // Add the success flag directly into the userData map
        userData.put("success", true);
        
        // Return the flat map directly!
        return ResponseEntity.ok(userData);
    }

    // 2. UNLOCK THE DASHBOARD: Send their active applications
    @GetMapping("/applications")
    public ResponseEntity<?> getMyApplications(Authentication authentication) {
        Applicant applicant = applicantRepository.findByUser_Email(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Applicant not found"));
        
        List<Application> apps = applicationService.getApplicationsByApplicant(applicant.getApplicantId());
        
        return ResponseEntity.ok(Map.of("data", apps, "success", true));
    }
}
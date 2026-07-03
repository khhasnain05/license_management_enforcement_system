package com.dlms.controller;

import com.dlms.model.Application;
import com.dlms.repository.ApplicationRepository;
import com.dlms.repository.ApplicantRepository;
import com.dlms.model.Applicant;
import com.dlms.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * =============================================
 * CONTROLLER — ApplicantTestController
 * =============================================
 */
@RestController
@RequestMapping("/api/applicant/tests")
@RequiredArgsConstructor
public class ApplicantTestController {

    private final TestService           testService;
    private final ApplicationRepository applicationRepository;
    private final ApplicantRepository   applicantRepository;

    // ══════════════════════════════════════════════════════════════════════════
    //  LIST TESTS
    // ══════════════════════════════════════════════════════════════════════════

    @GetMapping
    public ResponseEntity<?> getTestsForApplication(
            @RequestParam Long applicationId) {
        try {
            List<Map<String, Object>> tests = testService.getTestsByApplication(applicationId);
            return ResponseEntity.ok(tests);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  SCHEDULE A TEST
    // ══════════════════════════════════════════════════════════════════════════

    @PostMapping("/schedule")
    public ResponseEntity<?> scheduleTest(
            @RequestBody Map<String, Object> payload,
            Principal principal) {

        try {
            Long applicationId = Long.valueOf(payload.get("applicationId").toString());
            String testType    = payload.get("testType").toString();
            String testCenter  = payload.get("testCenter").toString();
            String dateStr     = payload.get("testDate").toString();
            LocalDate testDate = LocalDate.parse(dateStr);

            Application application = applicationRepository.findById(applicationId)
                    .orElseThrow(() -> new RuntimeException(
                            "Application not found: " + applicationId));

            Applicant applicant = applicantRepository.findByUser_Email(principal.getName())
                    .orElseThrow(() -> new RuntimeException("Applicant not found"));

            if (!application.getApplicant().getApplicantId()
                    .equals(applicant.getApplicantId())) {
                return ResponseEntity.status(403)
                        .body(Map.of("success", false,
                                "message", "You do not own this application."));
            }

            testService.scheduleTest(application, testType, testCenter, testDate);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Test scheduled successfully for " + testDate
                            + " at " + testCenter + "."));

        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("success", false,
                            "message", "Could not schedule test: " + e.getMessage()));
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  BUSY DATES (for frontend date-picker)
    // ══════════════════════════════════════════════════════════════════════════

    @GetMapping("/busy-dates")
    public ResponseEntity<?> getBusyDates(
            @RequestParam String testCenter) {
        try {
            List<LocalDate> busyDates = testService.getBusyDates(testCenter);
            List<String> dateStrings = busyDates.stream()
                    .map(LocalDate::toString)
                    .toList();
            return ResponseEntity.ok(dateStrings);
        } catch (Exception e) {
            return ResponseEntity.ok(List.of());
        }
    }
}
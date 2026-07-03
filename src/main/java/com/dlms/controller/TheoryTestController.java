package com.dlms.controller;

import com.dlms.model.Application;
import com.dlms.model.Question;
import com.dlms.model.TheoryTestAttempt;
import com.dlms.model.enums.ApplicationStage;
import com.dlms.observer.NotificationSubject;
import com.dlms.repository.ApplicationRepository;
import com.dlms.repository.QuestionRepository;
import com.dlms.repository.TheoryTestAttemptRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tests/theory")
@RequiredArgsConstructor
public class TheoryTestController {

    private final QuestionRepository questionRepository;
    private final ApplicationRepository applicationRepository;
    private final TheoryTestAttemptRepository attemptRepository;
    private final NotificationSubject notificationSubject; // 🚨 INJECTION ADDED

    @GetMapping("/questions")
    public ResponseEntity<?> getQuestions() {
        List<Question> questions = questionRepository.findRandom5Questions();
        return ResponseEntity.ok(questions);
    }

    @PostMapping("/submit")
    public ResponseEntity<?> submitTest(@RequestBody com.dlms.dto.TestSubmissionRequest request) {
        Application app = applicationRepository.findById(request.getApplicationId())
                .orElseThrow(() -> new RuntimeException("Application not found"));

        // 1. Calculate attempt number
        int attempts = (app.getTheoryAttempts() != null ? app.getTheoryAttempts() : 0) + 1;
        app.setTheoryAttempts(attempts);
        boolean passed = request.getScore() >= 70;

        // 2. Create the Audit Record for the overall attempt
        TheoryTestAttempt attemptRecord = new TheoryTestAttempt();
        attemptRecord.setApplication(app);
        attemptRecord.setAttemptNumber(attempts);
        attemptRecord.setScore(request.getScore());
        attemptRecord.setPassed(passed);
        attemptRecord.setAttemptDate(java.time.LocalDateTime.now());
        
        // 3. Map their specific answers
        java.util.List<com.dlms.model.TheoryTestDetail> detailsList = new java.util.ArrayList<>();
        for (var answer : request.getAnswers()) {
            com.dlms.model.TheoryTestDetail detail = new com.dlms.model.TheoryTestDetail();
            detail.setAttempt(attemptRecord);
            
            Question q = questionRepository.findById(answer.getQuestionId()).orElse(null);
            detail.setQuestion(q);
            detail.setSelectedOption(answer.getSelectedOption());
            
            if (q != null && q.getCorrectOption().equals(answer.getSelectedOption())) {
                detail.setCorrect(true);
            } else {
                detail.setCorrect(false);
            }
            detailsList.add(detail);
        }
        attemptRecord.setDetails(detailsList);
        attemptRepository.save(attemptRecord);

        // 4. Handle the 3-Strikes Logic & added notifications
        if (passed) {
            app.setCurrentStage(ApplicationStage.PRACTICAL_PENDING);
            applicationRepository.save(app);
            
            notificationSubject.notifyObservers(
                    app.getApplicant().getUser().getUserId(), 
                    "TEST_RESULT",
                    "Theory Test Passed!",
                    "Congratulations! You passed with a score of " + request.getScore() + "%. You can now schedule your practical driving test."
            );
            
            return ResponseEntity.ok(Map.of("success", true, "passed", true, "message", "Congratulations! Practical Test Unlocked."));
        } else {
            if (attempts >= 3) {
                app.setCurrentStage(ApplicationStage.LEARNER_PAYMENT_PENDING);
                app.setApplicationStatus(com.dlms.model.enums.ApplicationStatus.PENDING);
                app.setTheoryAttempts(0); 
                applicationRepository.save(app);
                
                notificationSubject.notifyObservers(
                        app.getApplicant().getUser().getUserId(), 
                        "ERROR",
                        "Theory Test Limit Reached",
                        "You have failed the theory test 3 times (Latest score: " + request.getScore() + "%). Your application has been reset. You must apply and pay for a new Learner Permit."
                );
                
                return ResponseEntity.ok(Map.of("success", true, "passed", false, "reset", true, 
                    "message", "You have failed 3 times. Your learner permit is revoked. You must restart the application."));
            } else {
                applicationRepository.save(app);
                int left = 3 - attempts;
                
                notificationSubject.notifyObservers(
                        app.getApplicant().getUser().getUserId(), 
                        "TEST_RESULT",
                        "Theory Test Failed",
                        "You scored " + request.getScore() + "%. This was attempt " + attempts + " of 3. Please study and try again."
                );
                
                return ResponseEntity.ok(Map.of("success", true, "passed", false, "reset", false, 
                    "message", "Test failed. You have " + left + " attempt(s) remaining."));
            }
        }
    }
}
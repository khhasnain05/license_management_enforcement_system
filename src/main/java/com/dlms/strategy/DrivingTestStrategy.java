package com.dlms.strategy;

import com.dlms.model.Test;
import org.springframework.stereotype.Component;

/**
 * =============================================
 * STRATEGY PATTERN — CONCRETE STRATEGY 2
 * =============================================
 * DrivingTestStrategy: Evaluates practical/road driving test.
 * Pass/Fail is determined by the examiner's recorded status
 * (since practical tests are subjective evaluations by a human examiner).
 */
@Component
public class DrivingTestStrategy implements TestEvaluationStrategy {

    @Override
    public String evaluate(Test test) {
        // For practical tests, the examiner records the result directly
        // The 'status' field is set by the testing officer before calling evaluate()
        String currentStatus = test.getStatus();

        if ("PASSED".equalsIgnoreCase(currentStatus)) {
            System.out.println("[DRIVING-EVAL] Examiner marked PASSED. Comments: "
                    + test.getExaminerComments());
            return "PASSED";
        }

        System.out.println("[DRIVING-EVAL] Examiner marked FAILED. Comments: "
                + test.getExaminerComments());
        return "FAILED";
    }

    @Override
    public String getPassingCriteria() {
        return "Examiner must approve vehicle control, road safety, and parking maneuvers.";
    }
}

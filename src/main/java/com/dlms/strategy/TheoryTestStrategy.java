package com.dlms.strategy;

import com.dlms.model.Test;
import org.springframework.stereotype.Component;

/**
 * =============================================
 * STRATEGY PATTERN — CONCRETE STRATEGY 1
 * =============================================
 * TheoryTestStrategy: Evaluates MCQ-based written test.
 * Passing threshold: 70 out of 100 marks.
 */
@Component
public class TheoryTestStrategy implements TestEvaluationStrategy {

    private static final int PASSING_SCORE = 70;
    private static final int MAX_SCORE = 100;

    @Override
    public String evaluate(Test test) {
        if (test.getScore() == null) {
            return "FAILED"; // No score recorded = fail
        }

        // Simple threshold: 70% or above is a pass
        boolean passed = test.getScore() >= PASSING_SCORE;
        System.out.println("[THEORY-EVAL] Score: " + test.getScore()
                + "/" + MAX_SCORE + " → " + (passed ? "PASSED" : "FAILED"));

        return passed ? "PASSED" : "FAILED";
    }

    @Override
    public String getPassingCriteria() {
        return "Minimum " + PASSING_SCORE + " out of " + MAX_SCORE + " marks required to pass.";
    }
}

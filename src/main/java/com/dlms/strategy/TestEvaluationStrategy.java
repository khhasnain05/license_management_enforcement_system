package com.dlms.strategy;

import com.dlms.model.Test;

/**
 * =============================================
 * DESIGN PATTERN: STRATEGY PATTERN
 * =============================================
 * Purpose: Define a family of algorithms (test evaluation methods)
 * and make them interchangeable at runtime.
 *
 * Problem it solves: Theory test and Driving test are evaluated
 * VERY differently:
 *   - Theory test: score >= 70% → PASS (based on MCQ score)
 *   - Driving test: examiner comments + checklist → PASS/FAIL
 *
 * Without Strategy, you'd have messy if/else in the service.
 * With Strategy, each evaluation method is its own clean class.
 *
 * Usage:
 *   TestEvaluationContext context = new TestEvaluationContext(new TheoryTestStrategy());
 *   String result = context.evaluate(test);
 *
 *   context.setStrategy(new DrivingTestStrategy());
 *   result = context.evaluate(test); // Same call, different behavior
 */
public interface TestEvaluationStrategy {

    /**
     * Evaluates the test and returns "PASSED" or "FAILED".
     *
     * @param test The Test entity with score/comments already set
     * @return "PASSED" or "FAILED"
     */
    String evaluate(Test test);

    /**
     * Returns a human-readable description of the evaluation criteria.
     * Useful for displaying rules to the user.
     */
    String getPassingCriteria();
}

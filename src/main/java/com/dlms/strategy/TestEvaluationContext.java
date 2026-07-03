package com.dlms.strategy;

import com.dlms.model.Test;
import org.springframework.stereotype.Component;

/**
 * =============================================
 * STRATEGY PATTERN — CONTEXT
 * =============================================
 * TestEvaluationContext is the "context" that uses a strategy.
 * The caller sets a strategy then calls evaluate().
 * The context doesn't care HOW the test is evaluated — it delegates
 * entirely to whichever strategy is currently set.
 *
 * Usage example in TestService:
 *
 *   if (test.getTestType().equals("THEORY")) {
 *       evaluationContext.setStrategy(theoryTestStrategy);
 *   } else {
 *       evaluationContext.setStrategy(drivingTestStrategy);
 *   }
 *   String result = evaluationContext.evaluate(test);
 */
@Component
public class TestEvaluationContext {

    private TestEvaluationStrategy strategy;

    /** Inject default strategies via constructor */
    public TestEvaluationContext(TheoryTestStrategy theoryStrategy) {
        this.strategy = theoryStrategy; // Default strategy
    }

    /** Switch strategy at runtime based on test type */
    public void setStrategy(TestEvaluationStrategy strategy) {
        this.strategy = strategy;
    }

    /** Delegates evaluation to the current strategy */
    public String evaluate(Test test) {
        if (strategy == null) {
            throw new IllegalStateException("No evaluation strategy set!");
        }
        return strategy.evaluate(test);
    }

    /** Expose the criteria so it can be shown to users */
    public String getPassingCriteria() {
        return strategy.getPassingCriteria();
    }
}

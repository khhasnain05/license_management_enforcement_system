package com.dlms.model.enums;

public enum ApplicationStatus {
    PENDING,        // Just submitted
    UNDER_REVIEW,   // Being reviewed by officer
    IN_PROGRESS,    // Learner issued, waiting for 41 days/tests
    APPROVED,       // Approved, license will be issued
    REJECTED,       // Application rejected
    CANCELLED,       // Cancelled by applicant
}

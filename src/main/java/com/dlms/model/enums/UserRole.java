package com.dlms.model.enums;

/** Defines the roles a user can have in the system */
public enum UserRole {
    APPLICANT,          // Regular citizen applying for a license
    LICENSING_OFFICER,  // DLMS staff who approves/rejects applications
    TESTING_OFFICER,    // Conducts and evaluates tests
    TRAFFIC_POLICE,     // Law enforcement officer
    ADMIN
}

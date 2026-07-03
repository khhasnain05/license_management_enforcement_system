package com.dlms.factory;

import com.dlms.model.enums.UserRole;
import org.springframework.stereotype.Component;

@Component
public class UserFactory {

    private final ApplicantCreator applicantCreator;
    private final OfficerCreator officerCreator;
    private final EnforcementCreator enforcementCreator;

    public UserFactory(ApplicantCreator applicantCreator,
                       OfficerCreator officerCreator,
                       EnforcementCreator enforcementCreator) {
        this.applicantCreator = applicantCreator;
        this.officerCreator = officerCreator;
        this.enforcementCreator = enforcementCreator;
    }

    public UserCreator getCreator(UserRole role) {

        if (role == null) {
            throw new IllegalArgumentException("User role cannot be null");
        }

        return switch (role) {
            case APPLICANT -> applicantCreator;
            case LICENSING_OFFICER -> officerCreator;
            case TRAFFIC_POLICE -> enforcementCreator;
		default -> throw new IllegalArgumentException("Unexpected value: " + role);
        };
    }
}
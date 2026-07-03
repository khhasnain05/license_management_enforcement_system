package com.dlms.factory;

import com.dlms.model.User;
import com.dlms.model.enums.UserRole;
import org.springframework.stereotype.Component;

@Component
public class ApplicantCreator extends UserCreator {

    @Override
    protected UserRole getRole() {
        return UserRole.APPLICANT;
    }

    @Override
    protected void customizeUser(User user) {

        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        // Applicants must complete profile after registration
        user.setAccountStatus("PENDING_PROFILE");
    }
}
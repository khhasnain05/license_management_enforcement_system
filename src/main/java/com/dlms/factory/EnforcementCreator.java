package com.dlms.factory;

import com.dlms.model.User;
import com.dlms.model.enums.UserRole;
import org.springframework.stereotype.Component;

@Component
public class EnforcementCreator extends UserCreator {

    @Override
    protected UserRole getRole() {
        return UserRole.TRAFFIC_POLICE;
    }

    @Override
    protected void customizeUser(User user) {

        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        user.setAccountStatus("ACTIVE");
    }
}
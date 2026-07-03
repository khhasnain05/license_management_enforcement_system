package com.dlms.factory;

import com.dlms.model.User;
import com.dlms.model.enums.UserRole;
import org.springframework.stereotype.Component;

@Component
public class OfficerCreator extends UserCreator {

    @Override
    protected UserRole getRole() {
        return UserRole.LICENSING_OFFICER;
    }

    @Override
    protected void customizeUser(User user) {

        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        user.setAccountStatus("ACTIVE");
    }
}
package com.dlms.factory;

import com.dlms.model.User;
import com.dlms.model.enums.UserRole;

public abstract class UserCreator {

    public User createUser(String cnic,
                           String fullName,
                           String email,
                           String passwordHash,
                           String mobile) {

        if (cnic == null || email == null) {
            throw new IllegalArgumentException("CNIC and Email cannot be null");
        }

        User user = User.builder()
                .cnic(cnic)
                .fullNameEn(fullName)
                .email(email)
                .passwordHash(passwordHash)
                .mobileNumber(mobile)
                .accountStatus("ACTIVE")
                .role(getRole())
                .build();

        customizeUser(user);

        return user;
    }

    protected abstract UserRole getRole();

    protected void customizeUser(User user) {
        // default hook (optional override)
    }
}
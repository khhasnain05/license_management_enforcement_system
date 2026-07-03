package com.dlms.service;

import com.dlms.dto.RegisterRequest;
import com.dlms.model.User;
import com.dlms.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    // =========================
    // REGISTER USER
    // =========================
    @Transactional
    public User registerUser(RegisterRequest request) {
        // 1. Password encryption stays in Java
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // 2. Call PL/SQL Procedure
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("SP_REGISTER_USER");

        Map<String, Object> inParams = Map.of(
                "p_cnic", request.getCnic(),
                "p_full_name", request.getFullNameEn(),
                "p_email", request.getEmail(),
                "p_password_hash", hashedPassword,
                "p_mobile", request.getMobileNumber(),
                "p_district", request.getDistrict(),
                "p_gender", request.getGender(),
                "p_blood_group", request.getBloodGroup()
        );

        try {
            // Execute and get the generated User ID out of Oracle
            Map<String, Object> out = jdbcCall.execute(inParams);
            Long generatedUserId = ((Number) out.get("P_USER_ID")).longValue();
            
            // Return the user from the database
            return userRepository.findById(generatedUserId).orElseThrow();
        } catch (Exception e) {
            throw new IllegalArgumentException("Registration failed: " + e.getMessage());
        }
    }

    // =========================
    // FIND USER
    // =========================
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findById(Long userId) {
        return userRepository.findById(userId);
    }

    // =========================
    // PASSWORD CHECK
    // =========================
    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
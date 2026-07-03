package com.dlms.service;

import com.dlms.dto.StaffRequest;
import com.dlms.model.LicensingOfficer;
import com.dlms.model.TestingOfficer;
import com.dlms.model.TrafficPoliceOfficer;
import com.dlms.model.User;
import com.dlms.model.enums.UserRole;
import com.dlms.repository.LicensingOfficerRepository;
import com.dlms.repository.TestingOfficerRepository;
import com.dlms.repository.TrafficPoliceOfficerRepository;
import com.dlms.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminManagementService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LicensingOfficerRepository licensingOfficerRepository;
    private final TestingOfficerRepository testingOfficerRepository;
    private final TrafficPoliceOfficerRepository trafficPoliceOfficerRepository;
    private final JdbcTemplate jdbcTemplate;

    public List<User> getAllStaff() {
        List<UserRole> staffRoles = Arrays.asList(
                UserRole.LICENSING_OFFICER, 
                UserRole.TESTING_OFFICER, 
                UserRole.TRAFFIC_POLICE // Fallback based on your enums
        );
        return userRepository.findByRoleIn(staffRoles);
    }

    @Transactional
    public User createStaff(StaffRequest request) {
        // 1. Check if Email exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists in the system.");
        }

        // 2. Validate and Check CNIC
        String cnic = request.getCnic();
        if (cnic == null || cnic.trim().isEmpty()) {
            throw new IllegalArgumentException("CNIC is required.");
        }
        if (userRepository.findByCnic(cnic).isPresent()) {
            throw new IllegalArgumentException("This CNIC is already registered to another user.");
        }

        // 3. Create the user using the provided CNIC
        User staff = User.builder()
                .fullNameEn(request.getFullNameEn())
                .email(request.getEmail())
                .cnic(cnic) // Uses the exact CNIC you typed in the frontend
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .accountStatus("ACTIVE")
                .build();

        User savedUser = userRepository.save(staff);

        // Create role-specific record
        switch (request.getRole()) {
            case LICENSING_OFFICER:
                LicensingOfficer lo = LicensingOfficer.builder()
                        .user(savedUser)
                        .department(request.getDepartment() != null ? request.getDepartment() : "General")
                        .build();
                licensingOfficerRepository.save(lo);
                break;

            case TESTING_OFFICER:
                TestingOfficer to = TestingOfficer.builder()
                        .user(savedUser)
                        .assignedCenter(request.getAssignedCenter() != null ? request.getAssignedCenter() : "Unknown Center")
                        .build();
                testingOfficerRepository.save(to);
                break;

            case TRAFFIC_POLICE:
                TrafficPoliceOfficer tpo = new TrafficPoliceOfficer();
                tpo.setUser(savedUser);
                tpo.setBadgeNumber(request.getBadgeNumber() != null ? request.getBadgeNumber() : "B" + System.currentTimeMillis());
                tpo.setStationName(request.getStationName() != null ? request.getStationName() : "Unknown Station");
                tpo.setRank(request.getRank() != null ? request.getRank() : "Constable");
                trafficPoliceOfficerRepository.save(tpo);
                break;

            default:
                throw new IllegalArgumentException("Unsupported role for staff creation");
        }

        return savedUser;
    }

    @Transactional
    public User updateStaff(Long id, StaffRequest request) {
        String newPasswordHash = null;
        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            newPasswordHash = passwordEncoder.encode(request.getPassword());
        }

        try {
            jdbcTemplate.update("CALL SP_UPDATE_STAFF(?, ?, ?, ?, ?)",
                id, request.getFullNameEn(), request.getEmail(), 
                request.getRole().name(), newPasswordHash
            );
            // Fetch and return updated user for frontend
            return userRepository.findById(id).orElseThrow();
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage()); // Passes Oracle error to frontend
        }
    }

    @Transactional
    public void toggleSuspend(Long id) {
        jdbcTemplate.update("CALL SP_TOGGLE_STAFF_SUSPEND(?)", id);
    }
    
    public List<Map<String, Object>> getGlobalOfficerAuditLog() {
        // Fetches the unified view, sorted by the most recent actions first
        String sql = "SELECT * FROM V_GLOBAL_OFFICER_AUDIT ORDER BY ACTION_DATE DESC";
        return jdbcTemplate.queryForList(sql);
    }
}
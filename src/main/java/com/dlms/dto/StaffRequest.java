package com.dlms.dto;

import com.dlms.model.enums.UserRole;
import lombok.Data;

@Data
public class StaffRequest {
    private String fullNameEn;
    private String email;
    private String password;
    private UserRole role;
    private String cnic; 

    // For TESTING_OFFICER
    private String assignedCenter;

    // For TRAFFIC_POLICE
    private String badgeNumber;
    private String stationName;
    private String rank;

    // For LICENSING_OFFICER
    private String department;
}
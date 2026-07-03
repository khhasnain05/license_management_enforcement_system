package com.dlms.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/** DTO for user registration request */
@Data
public class RegisterRequest {

    @NotBlank(message = "CNIC is required")
    @Pattern(regexp = "\\d{5}-\\d{7}-\\d{1}", message = "CNIC format: 12345-1234567-1")
    private String cnic;

    @NotBlank(message = "Full name is required")
    private String fullNameEn;

    @Email(message = "Valid email required")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @Pattern(regexp = "\\d{11}", message = "Mobile must be 11 digits")
    private String mobileNumber;

    private String fatherName;
    private String dob;
    private String gender;
    private String bloodGroup;
    private String district;
    private String role;
}

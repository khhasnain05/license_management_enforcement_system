package com.dlms.model;

import com.dlms.model.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "DLMS_USER")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", sequenceName = "USER_SEQ", allocationSize = 1)
    @Column(name = "USER_ID")
    private Long userId;

    @Column(name = "CNIC", unique = true, nullable = false, length = 15)
    private String cnic;

    @Column(name = "FULL_NAME_EN", nullable = false)
    private String fullNameEn;

    @Column(name = "FULL_NAME_UR")
    private String fullNameUr;

    @Column(name = "FATHER_NAME")
    private String fatherName;

    @Column(name = "DOB")
    private LocalDate dob;

    @Column(name = "GENDER", length = 10)
    private String gender;

    @Column(name = "BLOOD_GROUP", length = 5)
    private String bloodGroup;

    @Column(name = "MOBILE_NUMBER", length = 15)
    private String mobileNumber;

    @Column(name = "EMAIL", unique = true, nullable = false)
    private String email;

    @JsonIgnore
    @Column(name = "PASSWORD_HASH", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "ROLE", nullable = false)
    private UserRole role;

    @Column(name = "ACCOUNT_STATUS", length = 20)
    private String accountStatus;

    @Column(name = "DISTRICT")
    private String district;
}
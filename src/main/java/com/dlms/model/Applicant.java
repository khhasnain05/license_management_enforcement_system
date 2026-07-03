package com.dlms.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "APPLICANT")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Applicant {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "applicant_seq")
    @SequenceGenerator(name = "applicant_seq", sequenceName = "APPLICANT_SEQ", allocationSize = 1)
    @Column(name = "APPLICANT_ID")
    private Long applicantId;

    // One Applicant is linked to one User account
    @OneToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    @Column(name = "PREF_LANGUAGE", length = 10)
    private String prefLanguage;

    @Column(name = "REG_DATE")
    private LocalDate regDate;
}

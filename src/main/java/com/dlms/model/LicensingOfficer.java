package com.dlms.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "LICENSING_OFFICER")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class LicensingOfficer {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "officer_seq")
    @SequenceGenerator(name = "officer_seq", sequenceName = "OFFICER_SEQ", allocationSize = 1)
    @Column(name = "OFFICER_ID")
    private Long officerId;

    @OneToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    @Column(name = "DEPARTMENT")
    private String department;
}

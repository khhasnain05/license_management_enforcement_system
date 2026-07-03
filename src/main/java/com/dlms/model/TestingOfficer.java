package com.dlms.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "TESTING_OFFICER")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestingOfficer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long officerId;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // This is the magic field that will filter their dashboard
    // e.g., "Lahore Motor Registration Authority"
    @Column(name = "assigned_center")
    private String assignedCenter; 
}
package com.dlms.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "driving_test_schedule")
public class DrivingTestSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "application_id", unique = true)
    private Application application;
    
    @ManyToOne
    @JoinColumn(name = "examiner_officer_id")
    private TestingOfficer examiner;

    private LocalDate scheduledDate;
    private String testCenter;
    private String status; // e.g., "SCHEDULED", "COMPLETED"
}
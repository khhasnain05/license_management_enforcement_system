package com.dlms.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "practical_test_result")
public class PracticalTestResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "application_id", unique = true)
    private Application application;

    private Integer parkingScore;
    private Integer laneScore;
    private Integer signScore;
    private Integer totalScore;

    private String status; // Will always be "PASSED"
    private LocalDateTime evaluationDate;
}
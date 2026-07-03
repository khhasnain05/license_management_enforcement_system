package com.dlms.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.Immutable;

@Entity
@Immutable // Tells Hibernate this is a read-only Oracle View
@Table(name = "VW_CENTER_PERFORMANCE")
@Data
public class CenterPerformanceView {

    @Id // Test center name is unique per row in our GROUP BY view
    @Column(name = "TEST_CENTER")
    private String testCenter;

    @Column(name = "TOTAL_TESTS_CONDUCTED")
    private Long totalTestsConducted;

    @Column(name = "TOTAL_PASSED")
    private Long totalPassed;

    @Column(name = "TOTAL_FAILED")
    private Long totalFailed;

    @Column(name = "PASS_RATE_PERCENTAGE")
    private Double passRatePercentage;
}
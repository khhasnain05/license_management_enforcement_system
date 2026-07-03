package com.dlms.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class TheoryTestAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "application_id")
    private Application application;

    private int attemptNumber;
    private int score;
    private boolean passed;
    private LocalDateTime attemptDate;

    // Links to the specific answers they chose
    @OneToMany(mappedBy = "attempt", cascade = CascadeType.ALL)
    private List<TheoryTestDetail> details;
}
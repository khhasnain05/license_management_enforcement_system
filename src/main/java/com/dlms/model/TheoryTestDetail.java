package com.dlms.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class TheoryTestDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "attempt_id")
    private TheoryTestAttempt attempt;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    private String selectedOption; // What the user clicked (e.g., "A", "B", or null if they ran out of time)
    private boolean isCorrect;     // Whether it was right or wrong
}
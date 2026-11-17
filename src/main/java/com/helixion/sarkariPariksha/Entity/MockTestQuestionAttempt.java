package com.helixion.sarkariPariksha.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "mock_test_question_attempt")
@Data
public class MockTestQuestionAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Kis attempt ka question hai
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attempt_id", nullable = false)
    private MockTestAttempt mockTestAttempt;

    // Konsa question attempt kiya
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private QuestionMaster question;

    // Konsa option choose kiya (null agar skip kiya ya answer nahi diya)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_option_id")
    private QuestionOption selectedOption;

    // Is question par kitna time liya (seconds mein)
    @Column(name = "time_taken_seconds")
    private Integer timeTakenSeconds = 0;

    // Sahi choose kiya ki galat (true = Sahi, false = Galat, null = Skipped/Not Evaluated)
    @Column(name = "is_correct")
    private Boolean isCorrect;

    // Review ke liye mark kiya ya nahi
    @Column(name = "is_marked_for_review")
    private Boolean isMarkedForReview = false;

    // ANSWERED, NOT_ANSWERED, MARKED_FOR_REVIEW, SKIPPED
    @Column(name = "attempt_status", length = 30)
    private String attemptStatus;

    // Is question ke liye kitne marks mile (+ ya -)
    @Column(name = "marks_awarded")
    private Double marksAwarded = 0.0;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}


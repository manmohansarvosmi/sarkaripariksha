package com.helixion.sarkariPariksha.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "mock_test_attempt")
@Data
public class MockTestAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Kis candidate ne attempt kiya
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    private CandidateProfile candidate;

    // Konsa mock test diya
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mock_test_id", nullable = false)
    private MockTestMaster mockTest;

    @Column(name = "attempt_number")
    private Integer attemptNumber = 1;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "submit_time")
    private LocalDateTime submitTime;

    // Total kitna time lagaya (seconds mein)
    @Column(name = "total_time_taken_seconds")
    private Integer totalTimeTakenSeconds;

    @Column(name = "total_questions")
    private Integer totalQuestions;

    @Column(name = "total_attempted")
    private Integer totalAttempted = 0;

    @Column(name = "total_correct")
    private Integer totalCorrect = 0;

    @Column(name = "total_incorrect")
    private Integer totalIncorrect = 0;

    @Column(name = "total_skipped")
    private Integer totalSkipped = 0;

    // Total kitne marks mile
    @Column(name = "score")
    private Double score = 0.0;

    @Column(name = "total_marks")
    private Double totalMarks;

    @Column(name = "percentage")
    private Double percentage;

    @Column(name = "accuracy")
    private Double accuracy;

    // Mock test me candidate ki rank
    @Column(name = "candidate_rank")
    private Integer candidateRank;

    @Column(name = "percentile")
    private Double percentile;

    // IN_PROGRESS, SUBMITTED, TIMED_OUT
    @Column(name = "status", length = 30)
    private String status = "IN_PROGRESS";

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.startTime == null) {
            this.startTime = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}


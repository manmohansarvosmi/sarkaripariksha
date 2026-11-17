package com.helixion.sarkariPariksha.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "mock_test_leaderboard")
@Data
public class MockTestLeaderboard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Konsa mock test hai
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mock_test_id", nullable = false)
    private MockTestMaster mockTest;

    // Kis candidate ki rank hai
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    private CandidateProfile candidate;

    // Candidate ka attempt reference
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attempt_id", nullable = false)
    private MockTestAttempt mockTestAttempt;

    // Mock test mein overall rank (1, 2, 3...)
    @Column(name = "test_rank", nullable = false)
    private Integer testRank;

    // Candidate ke marks
    @Column(name = "score", nullable = false)
    private Double score;

    @Column(name = "total_marks")
    private Double totalMarks;

    @Column(name = "accuracy")
    private Double accuracy;

    // Test complete karne me kitna time lagaya (seconds)
    @Column(name = "total_time_taken_seconds")
    private Integer totalTimeTakenSeconds;

    @Column(name = "percentile")
    private Double percentile;

    // Total kitne participants mein se ye rank hai
    @Column(name = "total_participants")
    private Integer totalParticipants;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}


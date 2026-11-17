package com.helixion.sarkariPariksha.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "mock_test_analytics")
@Data
public class MockTestAnalytics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Konsa mock test hai
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mock_test_id", unique = true, nullable = false)
    private MockTestMaster mockTest;

    // Is mock test ko total kitne unique candidates ne diya
    @Column(name = "total_participants")
    private Integer totalParticipants = 0;

    // Is mock test ke total kitne attempts hue
    @Column(name = "total_attempts")
    private Integer totalAttempts = 0;

    // Is mock test ka topper/highest score
    @Column(name = "highest_score")
    private Double highestScore = 0.0;

    // Sabse kam score
    @Column(name = "lowest_score")
    private Double lowestScore = 0.0;

    // Average score sabhi students ka
    @Column(name = "average_score")
    private Double averageScore = 0.0;

    // Average time taken (seconds)
    @Column(name = "average_time_taken_seconds")
    private Integer averageTimeTakenSeconds = 0;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @PrePersist
    protected void onCreate() {
        this.lastUpdated = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.lastUpdated = LocalDateTime.now();
    }
}


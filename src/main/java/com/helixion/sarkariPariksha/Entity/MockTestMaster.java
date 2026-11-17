package com.helixion.sarkariPariksha.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "mock_test_master")
@Data
public class MockTestMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Exam Mapping (SSC CGL, IBPS PO, RRB NTPC etc.)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private ExamMaster exam;

    // Kis test series ke under aata hai ye mock test
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_series_id")
    private TestSeries testSeries;

    @Column(name = "test_name", nullable = false)
    private String testName;

    // FULL_LENGTH, SECTIONAL, TOPIC_WISE, PREVIOUS_YEAR
    @Column(name = "test_type", nullable = false)
    private String testType;

    @Column(name = "total_questions")
    private Integer totalQuestions;

    @Column(name = "total_marks")
    private BigDecimal totalMarks;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "negative_mark")
    private BigDecimal negativeMark;

    @Column(columnDefinition = "TEXT")
    private String instructions;

    // Whether this test is free or locked/paid
    @Column(name = "is_free")
    private Boolean free = false;

    @Column(name = "is_active")
    private Boolean active = true;

    // Admin who created the test
    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.active == null) {
            this.active = true;
        }
        if (this.free == null) {
            this.free = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

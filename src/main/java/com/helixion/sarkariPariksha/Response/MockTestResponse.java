package com.helixion.sarkariPariksha.Response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MockTestResponse {
    private Long id;

    // Exam & Category Details
    private Long examId;
    private String examName;
    private String examCode;
    private Long categoryId;
    private String categoryName;

    // Test Series Details
    private Long testSeriesId;
    private String testSeriesTitle;

    // Mock Test Details
    private String testName;
    private String testType;
    private Integer totalQuestions;
    private BigDecimal totalMarks;
    private Integer durationMinutes;
    private BigDecimal negativeMark;
    private String instructions;
    private Boolean free;
    private Boolean active;
    private Long createdBy;

    // Kitne logo ne attempt kiya (default 0)
    private Long totalAttempts = 0L;
    private Long totalCandidatesAttempted = 0L;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

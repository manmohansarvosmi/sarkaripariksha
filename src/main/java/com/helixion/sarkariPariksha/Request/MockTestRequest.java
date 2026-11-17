package com.helixion.sarkariPariksha.Request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MockTestRequest {
    private Long id;

    // Exam ID (SSC CGL, IBPS PO etc.)
    private Long examId;

    // Test Series ID (Optional - e.g. SSC CGL Master Test Series)
    private Long testSeriesId;

    private String testName;

    // FULL_LENGTH, SECTIONAL, TOPIC_WISE, PREVIOUS_YEAR
    private String testType;

    private Integer totalQuestions;

    private BigDecimal totalMarks;

    private Integer durationMinutes;

    private BigDecimal negativeMark;

    private String instructions;

    // Free test or paid
    private Boolean free = false;

    private Boolean active = true;

    private Long createdBy;
}

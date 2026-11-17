package com.helixion.sarkariPariksha.Response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExamResponse {
    private Long id;

    // Category details
    private Long categoryId;
    private String categoryName;
    private String categoryCode;

    // Exam details
    private String examName;
    private String examCode;
    private String description;
    private String iconUrl;
    private String eligibility;
    private Integer displayOrder;
    private Boolean active;

    // Is exam ke kitne mock test papers available hain (default 0)
    private Long totalMockTests = 0L;

    // Is exam ke mock tests kitne logo ne attempt kiye (default 0)
    private Long totalAttempts = 0L;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

package com.helixion.sarkariPariksha.Response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExamCategoryResponse {
    private Long id;

    // Example: SSC, Banking, Railways, Defence
    private String categoryName;

    // Example: SSC, BANK, RAILWAY, DEFENCE
    private String categoryCode;

    private String description;

    private String iconUrl;

    private Integer displayOrder;

    private Boolean active;

    // Total active exams available under this category
    private Integer totalExams;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


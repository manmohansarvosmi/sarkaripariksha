package com.helixion.sarkariPariksha.Response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SubjectResponse {
    private Long id;

    // Exam Information
    private Long examId;
    private String examName;
    private String examCode;

    // Subject Information
    private String subjectName;
    private String subjectCode;
    private String description;
    private Integer displayOrder;
    private Boolean active;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


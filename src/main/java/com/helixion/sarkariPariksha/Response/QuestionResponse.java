package com.helixion.sarkariPariksha.Response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class QuestionResponse {
    private Long id;

    // Mock Test & Exam Details
    private Long mockTestId;
    private String testName;
    private Long examId;
    private String examName;

    // Subject Details
    private Long subjectId;
    private String subjectName;

    // Question Details
    private String questionText;
    private String questionType;
    private String difficultyLevel;
    private String answerType;
    private Double marks;
    private Double negativeMarks;
    private String explanation;
    private String questionImageUrl;
    private Boolean active;

    // Options List
    private List<QuestionOptionResponse> options = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


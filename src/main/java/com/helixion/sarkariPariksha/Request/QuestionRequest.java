package com.helixion.sarkariPariksha.Request;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class QuestionRequest {
    private Long id;

    // Konsa mock test hai
    private Long mockTestId;

    // Optional Subject (Quantitative, Reasoning, etc.)
    private Long subjectId;

    private String questionText;

    // TEXT / IMAGE
    private String questionType = "TEXT";

    // EASY / MEDIUM / HARD
    private String difficultyLevel = "MEDIUM";

    // SINGLE_CHOICE / MULTIPLE_CHOICE
    private String answerType = "SINGLE_CHOICE";

    private Double marks = 1.0;

    private Double negativeMarks = 0.25;

    private String explanation;

    private String questionImageUrl;

    private Boolean active = true;

    // List of options (A, B, C, D)
    private List<QuestionOptionRequest> options = new ArrayList<>();
}


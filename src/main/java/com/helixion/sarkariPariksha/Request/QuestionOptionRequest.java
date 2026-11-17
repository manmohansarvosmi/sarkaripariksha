package com.helixion.sarkariPariksha.Request;

import lombok.Data;

@Data
public class QuestionOptionRequest {
    private Long id;
    private String optionCode; // A, B, C, D
    private String optionText;
    private String optionImageUrl;
    private Boolean isCorrect = false;
    private Integer displayOrder;
}


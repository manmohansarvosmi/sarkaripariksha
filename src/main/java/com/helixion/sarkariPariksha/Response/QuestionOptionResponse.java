package com.helixion.sarkariPariksha.Response;

import lombok.Data;

@Data
public class QuestionOptionResponse {
    private Long id;
    private String optionCode;
    private String optionText;
    private String optionImageUrl;
    private Integer displayOrder;
}


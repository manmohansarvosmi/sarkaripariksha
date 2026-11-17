package com.helixion.sarkariPariksha.Request;

import lombok.Data;

@Data
public class ExamRequest {
    private Long id;

    // Kis category ke under banega (e.g. SSC, Banking, Railways category id)
    private Long categoryId;

    private String examName;

    private String examCode;

    private String description;

    private String iconUrl;

    private String eligibility;

    private Integer displayOrder;

    private Boolean active = true;
}


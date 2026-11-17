package com.helixion.sarkariPariksha.Request;

import lombok.Data;

@Data
public class SubjectRequest {
    private Long id;

    // Exam to which this subject belongs (e.g. SSC CGL, IBPS PO)
    private Long examId;

    // Example: Quantitative Aptitude, Reasoning, English, General Awareness
    private String subjectName;

    // Example: QUANT, REASONING, ENGLISH, GA
    private String subjectCode;

    private String description;

    private Integer displayOrder = 0;

    private Boolean active = true;
}


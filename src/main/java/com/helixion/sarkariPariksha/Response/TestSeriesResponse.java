package com.helixion.sarkariPariksha.Response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TestSeriesResponse {
    private Long id;

    // Exam & Category Details
    private Long examId;
    private String examName;
    private String examCode;
    private Long categoryId;
    private String categoryName;

    // Test Series Details
    private String title;
    private String subTitle;
    private String description;
    private String iconUrl;
    private String bannerUrl;
    private String language;
    private Double rating;
    private Long totalEnrolled;
    private Integer totalTests;
    private Integer totalFreeTests;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private Boolean isFree;
    private Integer displayOrder;
    private Boolean active;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


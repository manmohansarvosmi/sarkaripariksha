package com.helixion.sarkariPariksha.Request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TestSeriesRequest {
    private Long id;

    // Kis exam ke under banegi (e.g. SSC CGL, SSC CHSL)
    private Long examId;

    // e.g. "SSC CGL 2025 Tier-1 & Tier-2 Master Test Series"
    private String title;

    // e.g. "Latest 2026 TCS Pattern • Video Solutions"
    private String subTitle;

    private String description;

    private String iconUrl;

    private String bannerUrl;

    // e.g. "Bilingual", "Hindi", "English"
    private String language;

    // e.g. 4.8
    private Double rating;

    // e.g. 142000
    private Long totalEnrolled;

    // e.g. 65
    private Integer totalTests;

    // e.g. 15
    private Integer totalFreeTests;

    private BigDecimal price;

    private BigDecimal discountPrice;

    private Boolean isFree;

    private Integer displayOrder;

    private Boolean active = true;
}


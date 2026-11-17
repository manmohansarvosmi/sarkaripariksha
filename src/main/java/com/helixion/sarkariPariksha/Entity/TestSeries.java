package com.helixion.sarkariPariksha.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "test_series")
@Data
public class TestSeries {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Kis exam ke under aati hai ye test series (e.g. SSC CGL, SSC CHSL)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private ExamMaster exam;

    // e.g. "SSC CGL 2025 Tier-1 & Tier-2 Master Test Series"
    @Column(name = "title", nullable = false)
    private String title;

    // e.g. "Latest 2026 TCS Pattern • Video Solutions"
    @Column(name = "sub_title")
    private String subTitle;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "icon_url")
    private String iconUrl;

    @Column(name = "banner_url")
    private String bannerUrl;

    // e.g. "Bilingual", "Hindi", "English"
    @Column(name = "language", length = 50)
    private String language = "Bilingual";

    // e.g. 4.8
    @Column(name = "rating")
    private Double rating = 4.8;

    // e.g. 142000 students enrolled
    @Column(name = "total_enrolled")
    private Long totalEnrolled = 0L;

    // Total kitne mock tests hain is test series me (e.g. 65)
    @Column(name = "total_tests")
    private Integer totalTests = 0;

    // Kitne tests free hain (e.g. 15 Free)
    @Column(name = "total_free_tests")
    private Integer totalFreeTests = 0;

    @Column(name = "price")
    private BigDecimal price = BigDecimal.ZERO;

    @Column(name = "discount_price")
    private BigDecimal discountPrice = BigDecimal.ZERO;

    @Column(name = "is_free")
    private Boolean free = false;

    @Column(name = "display_order")
    private Integer displayOrder = 0;

    @Column(name = "is_active")
    private Boolean active = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.active == null) {
            this.active = true;
        }
        if (this.rating == null) {
            this.rating = 4.8;
        }
        if (this.language == null) {
            this.language = "Bilingual";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}


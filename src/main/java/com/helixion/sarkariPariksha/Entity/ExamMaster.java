package com.helixion.sarkariPariksha.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "exam_master")
@Data
public class ExamMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Kis category ke under aata hai (SSC, Banking, Railways etc.)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private ExamCategory examCategory;

    // Example: SSC CGL, SSC CHSL, IBPS PO, RRB NTPC
    @Column(name = "exam_name", nullable = false)
    private String examName;

    // Example: SSC_CGL, SSC_CHSL, IBPS_PO, RRB_NTPC
    @Column(name = "exam_code", unique = true, nullable = false)
    private String examCode;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "icon_url")
    private String iconUrl;

    // Eligibility criteria: e.g. "Graduation", "12th Pass", "10th Pass"
    @Column(name = "eligibility")
    private String eligibility;

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
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

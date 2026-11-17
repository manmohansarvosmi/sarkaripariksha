package com.helixion.sarkariPariksha.Entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "question_option")
@Data
public class QuestionOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private QuestionMaster question;

    @Column(name = "option_text", columnDefinition = "TEXT",
            nullable = false)
    private String optionText;

    @Column(name = "option_image_url")
    private String optionImageUrl;

    @Column(name = "option_code", length = 5)
    private String optionCode; // A, B, C, D

    @Column(name = "is_correct")
    private Boolean correct = false;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "active")
    private Boolean active = true;
}


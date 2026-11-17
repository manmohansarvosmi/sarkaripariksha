package com.helixion.sarkariPariksha.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "question_master")
@Data
public class QuestionMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Subject Mapping (Quantitative, Reasoning, English etc.)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id")
    private Subject subject;

    // Konsa mock test ka question hai
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mock_test_id", nullable = false)
    private MockTestMaster mockTest;

    @Column(name = "question_text", columnDefinition = "TEXT", nullable = false)
    private String questionText;

    // TEXT / IMAGE
    @Column(name = "question_type", length = 20)
    private String questionType = "TEXT";

    // EASY / MEDIUM / HARD
    @Column(name = "difficulty_level", length = 20)
    private String difficultyLevel = "MEDIUM";

    // SINGLE_CHOICE / MULTIPLE_CHOICE
    @Column(name = "answer_type", length = 30)
    private String answerType = "SINGLE_CHOICE";

    @Column(name = "marks")
    private Double marks = 1.0;

    @Column(name = "negative_marks")
    private Double negativeMarks = 0.25;

    @Column(name = "explanation", columnDefinition = "TEXT")
    private String explanation;

    @Column(name = "question_image_url")
    private String questionImageUrl;

    @Column(name = "is_active")
    private Boolean active = true;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<QuestionOption> options = new ArrayList<>();

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

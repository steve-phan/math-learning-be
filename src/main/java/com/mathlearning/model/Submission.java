package com.mathlearning.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "submissions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(name = "original_image_url", nullable = false, columnDefinition = "TEXT")
    private String originalImageUrl;

    @Column(name = "annotated_image_url", columnDefinition = "TEXT")
    private String annotatedImageUrl;

    @Column(name = "ai_score", precision = 4, scale = 2)
    private BigDecimal aiScore;

    @Column(name = "is_correct")
    private Boolean isCorrect;

    @Column(name = "ai_feedback", columnDefinition = "TEXT")
    private String aiFeedback;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "correct_steps", columnDefinition = "jsonb")
    private List<String> correctSteps;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "topic_tags", columnDefinition = "jsonb")
    private List<String> topicTags;

    @Column(name = "processing_time_ms")
    private Integer processingTimeMs;

    @Column(name = "ai_provider")
    private String aiProvider;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}

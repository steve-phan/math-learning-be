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

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "questions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Builder.Default
    private String subject = "MATH";

    @Column(nullable = false)
    private String topic;

    @Column(name = "grade_level", nullable = false)
    private Integer gradeLevel;

    @Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
    private String questionText;

    @Column(name = "question_image_url", columnDefinition = "TEXT")
    private String questionImageUrl;

    @Column(name = "correct_answer", nullable = false, columnDefinition = "TEXT")
    private String correctAnswer;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "solution_steps", columnDefinition = "jsonb")
    private List<String> solutionSteps;

    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum Difficulty {
        EASY, MEDIUM, HARD
    }
}

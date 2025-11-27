package com.mathlearning.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionResponse {
    private Long submissionId;
    private BigDecimal score;
    private Boolean correct;
    private String feedback;
    private List<String> correctSteps;
    private List<String> topicTags;
    private Integer xpEarned;
    private Integer totalXp;
    private Integer currentStreak;
    private Integer processingTimeMs;
}

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
public class GradingResult {
    private BigDecimal score;
    private Boolean correct;
    private String feedback;
    private List<String> correctSteps;
    private List<String> topicTags;
    private Integer processingTimeMs;
    private String aiProvider;
}

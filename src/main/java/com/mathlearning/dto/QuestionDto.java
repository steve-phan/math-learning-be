package com.mathlearning.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDto {
    private Long id;
    private String subject;
    private String topic;
    private Integer gradeLevel;
    private String questionText;
    private String questionImageUrl;
    private String difficulty;
}

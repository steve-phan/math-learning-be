package com.mathlearning.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MistakeDto {
    private Long id;
    private Long submissionId;
    private String questionText;
    private String topic;
    private LocalDateTime createdAt;
    private Boolean reviewed;
}

package com.mathlearning.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressDto {
    private Integer totalXp;
    private Integer currentStreak;
    private Integer longestStreak;
    private Integer totalSubmissions;
    private Integer correctSubmissions;
    private Double accuracy;
}

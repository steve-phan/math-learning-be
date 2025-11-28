package com.mathlearning.controller;

import com.mathlearning.dto.ApiResponse;
import com.mathlearning.dto.MistakeDto;
import com.mathlearning.dto.ProgressDto;
import com.mathlearning.model.MistakeNotebook;
import com.mathlearning.model.UserProgress;
import com.mathlearning.repository.MistakeNotebookRepository;
import com.mathlearning.repository.SubmissionRepository;
import com.mathlearning.repository.UserProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
public class ProgressController {

        private final UserProgressRepository userProgressRepository;
        private final SubmissionRepository submissionRepository;
        private final MistakeNotebookRepository mistakeNotebookRepository;

        @GetMapping
        public ResponseEntity<ApiResponse<ProgressDto>> getProgress(Authentication authentication) {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                Long userId = (Long) (auth != null ? auth.getPrincipal() : null);

                UserProgress progress = userProgressRepository.findByUserId(userId)
                                .orElseGet(() -> UserProgress.builder()
                                                .totalXp(0)
                                                .currentStreak(0)
                                                .longestStreak(0)
                                                .build());

                long totalSubmissions = submissionRepository.countByUserIdAndIsCorrect(userId, true) +
                                submissionRepository.countByUserIdAndIsCorrect(userId, false);
                long correctSubmissions = submissionRepository.countByUserIdAndIsCorrect(userId, true);

                double accuracy = totalSubmissions > 0 ? (correctSubmissions * 100.0 / totalSubmissions) : 0.0;

                ProgressDto dto = ProgressDto.builder()
                                .totalXp(progress.getTotalXp())
                                .currentStreak(progress.getCurrentStreak())
                                .longestStreak(progress.getLongestStreak())
                                .totalSubmissions((int) totalSubmissions)
                                .correctSubmissions((int) correctSubmissions)
                                .accuracy(accuracy)
                                .build();

                return ResponseEntity.ok(ApiResponse.success(dto));
        }

        @GetMapping("/mistakes")
        public ResponseEntity<ApiResponse<List<MistakeDto>>> getMistakes(Authentication authentication) {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                Long userId = (Long) (auth != null ? auth.getPrincipal() : null);

                List<MistakeNotebook> mistakes = mistakeNotebookRepository.findByUserIdAndReviewed(userId, false);

                List<MistakeDto> mistakeDtos = mistakes.stream()
                                .map(mistake -> MistakeDto.builder()
                                                .id(mistake.getId())
                                                .submissionId(mistake.getSubmission().getId())
                                                .questionText(mistake.getSubmission().getQuestion().getQuestionText())
                                                .topic(mistake.getSubmission().getQuestion().getTopic())
                                                .createdAt(mistake.getCreatedAt())
                                                .reviewed(mistake.getReviewed())
                                                .build())
                                .collect(Collectors.toList());

                return ResponseEntity.ok(ApiResponse.success(mistakeDtos));
        }
}

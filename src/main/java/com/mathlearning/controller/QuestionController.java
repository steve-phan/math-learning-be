package com.mathlearning.controller;

import com.mathlearning.dto.ApiResponse;
import com.mathlearning.dto.QuestionDto;
import com.mathlearning.model.Question;
import com.mathlearning.model.User;
import com.mathlearning.repository.QuestionRepository;
import com.mathlearning.repository.UserRepository;
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
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

    @GetMapping("/daily")
    public ResponseEntity<ApiResponse<List<QuestionDto>>> getDailyQuestions(Authentication authentication) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) (auth != null ? auth.getPrincipal() : null);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // For MVP, return all questions for user's grade level
        // In production, you'd implement more sophisticated logic (adaptive learning,
        // etc.)
        List<Question> questions = questionRepository.findByGradeLevel(user.getGradeLevel());

        List<QuestionDto> questionDtos = questions.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(questionDtos));
    }

    private QuestionDto toDto(Question question) {
        return QuestionDto.builder()
                .id(question.getId())
                .subject(question.getSubject())
                .topic(question.getTopic())
                .gradeLevel(question.getGradeLevel())
                .questionText(question.getQuestionText())
                .questionImageUrl(question.getQuestionImageUrl())
                .difficulty(question.getDifficulty() != null ? question.getDifficulty().toString() : null)
                .build();
    }
}

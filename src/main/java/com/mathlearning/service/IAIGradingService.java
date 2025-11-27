package com.mathlearning.service;

import com.mathlearning.dto.GradingResult;

/**
 * Service interface for AI-powered grading operations.
 */
public interface IAIGradingService {

    /**
     * Grades a student's submission using AI.
     *
     * @param imageUrl      the URL of the uploaded solution image
     * @param questionText  the text of the question
     * @param correctAnswer the correct answer for validation
     * @param gradeLevel    the student's grade level
     * @return grading result with score, feedback, and solution steps
     * @throws com.mathlearning.exception.AIGradingException if AI grading fails
     */
    GradingResult gradeSubmission(String imageUrl, String questionText, String correctAnswer, Integer gradeLevel);
}

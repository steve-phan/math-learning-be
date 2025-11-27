package com.mathlearning.service;

import com.mathlearning.dto.SubmissionDto;
import com.mathlearning.dto.SubmissionResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Service interface for submission management operations.
 */
public interface ISubmissionService {

    /**
     * Creates a new submission with photo upload and AI grading.
     *
     * @param userId     the ID of the user submitting
     * @param questionId the ID of the question being answered
     * @param image      the uploaded solution image
     * @return submission response with grading results and XP earned
     * @throws com.mathlearning.exception.ResourceNotFoundException if user or
     *                                                              question not
     *                                                              found
     * @throws com.mathlearning.exception.InvalidRequestException   if image is
     *                                                              invalid
     */
    SubmissionResponse createSubmission(Long userId, Long questionId, MultipartFile image);

    /**
     * Retrieves all submissions for a user.
     *
     * @param userId the ID of the user
     * @return list of submission DTOs
     */
    List<SubmissionDto> getUserSubmissions(Long userId);

    /**
     * Retrieves a specific submission by ID.
     *
     * @param submissionId the submission ID
     * @param userId       the user ID for authorization check
     * @return submission DTO
     * @throws com.mathlearning.exception.ResourceNotFoundException if submission
     *                                                              not found
     * @throws com.mathlearning.exception.InvalidRequestException   if user doesn't
     *                                                              own the
     *                                                              submission
     */
    SubmissionDto getSubmission(Long submissionId, Long userId);
}

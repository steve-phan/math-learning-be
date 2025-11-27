package com.mathlearning.service.impl;

import com.mathlearning.dto.GradingResult;
import com.mathlearning.dto.SubmissionDto;
import com.mathlearning.dto.SubmissionResponse;
import com.mathlearning.exception.InvalidRequestException;
import com.mathlearning.exception.ResourceNotFoundException;
import com.mathlearning.model.*;
import com.mathlearning.repository.*;
import com.mathlearning.service.IAIGradingService;
import com.mathlearning.service.ISubmissionService;
import com.mathlearning.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubmissionServiceImpl implements ISubmissionService {

    private final SubmissionRepository submissionRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final UserProgressRepository userProgressRepository;
    private final MistakeNotebookRepository mistakeNotebookRepository;
    private final IAIGradingService aiGradingService;
    private final StorageService storageService;

    @Override
    @Transactional
    public SubmissionResponse createSubmission(Long userId, Long questionId, MultipartFile image) {
        log.debug("Creating submission for user: {}, question: {}", userId, questionId);

        // Validate image
        if (image == null || image.isEmpty()) {
            throw new InvalidRequestException("Image file is required");
        }

        // Fetch user and question
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", questionId));

        try {
            // Upload image to storage
            String imageUrl = storageService.uploadFile(image, "submissions");
            log.debug("Image uploaded successfully: {}", imageUrl);

            // Grade with AI
            GradingResult gradingResult = aiGradingService.gradeSubmission(
                    imageUrl,
                    question.getQuestionText(),
                    question.getCorrectAnswer(),
                    question.getGradeLevel());

            // Create submission record
            Submission submission = Submission.builder()
                    .user(user)
                    .question(question)
                    .originalImageUrl(imageUrl)
                    .aiScore(gradingResult.getScore())
                    .isCorrect(gradingResult.getCorrect())
                    .aiFeedback(gradingResult.getFeedback())
                    .correctSteps(gradingResult.getCorrectSteps())
                    .topicTags(gradingResult.getTopicTags())
                    .processingTimeMs(gradingResult.getProcessingTimeMs())
                    .aiProvider(gradingResult.getAiProvider())
                    .build();

            submission = submissionRepository.save(submission);

            // Calculate XP reward
            int xpEarned = calculateXp(gradingResult.getScore(), question.getDifficulty());
            log.debug("XP calculated: {} (score: {}, difficulty: {})",
                    xpEarned, gradingResult.getScore(), question.getDifficulty());

            // Update user progress
            UserProgress progress = userProgressRepository.findByUserId(userId)
                    .orElseGet(() -> {
                        log.info("Creating new progress record for user: {}", userId);
                        UserProgress newProgress = UserProgress.builder()
                                .userId(userId)
                                .user(user)
                                .totalXp(0)
                                .currentStreak(0)
                                .longestStreak(0)
                                .build();
                        return userProgressRepository.save(newProgress);
                    });

            progress.setTotalXp(progress.getTotalXp() + xpEarned);

            // Update streak
            updateStreak(progress);

            userProgressRepository.save(progress);
            log.info("User progress updated - XP: {}, Streak: {}",
                    progress.getTotalXp(), progress.getCurrentStreak());

            // Add to mistake notebook if incorrect
            if (!gradingResult.getCorrect()) {
                MistakeNotebook mistake = MistakeNotebook.builder()
                        .user(user)
                        .submission(submission)
                        .reviewed(false)
                        .build();
                mistakeNotebookRepository.save(mistake);
                log.debug("Added submission to mistake notebook");
            }

            // Build response
            return SubmissionResponse.builder()
                    .submissionId(submission.getId())
                    .score(submission.getAiScore())
                    .correct(submission.getIsCorrect())
                    .feedback(submission.getAiFeedback())
                    .correctSteps(submission.getCorrectSteps())
                    .topicTags(submission.getTopicTags())
                    .xpEarned(xpEarned)
                    .totalXp(progress.getTotalXp())
                    .currentStreak(progress.getCurrentStreak())
                    .processingTimeMs(submission.getProcessingTimeMs())
                    .build();

        } catch (InvalidRequestException | ResourceNotFoundException e) {
            throw e; // Re-throw custom exceptions
        } catch (Exception e) {
            log.error("Error creating submission", e);
            throw new InvalidRequestException("Failed to process submission: " + e.getMessage());
        }
    }

    @Override
    public List<SubmissionDto> getUserSubmissions(Long userId) {
        log.debug("Fetching submissions for user: {}", userId);
        List<Submission> submissions = submissionRepository.findByUserIdOrderByCreatedAtDesc(userId);
        log.debug("Found {} submissions for user: {}", submissions.size(), userId);

        return submissions.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public SubmissionDto getSubmission(Long submissionId, Long userId) {
        log.debug("Fetching submission: {} for user: {}", submissionId, userId);

        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission", "id", submissionId));

        // Verify ownership
        if (!submission.getUser().getId().equals(userId)) {
            log.warn("Unauthorized access attempt to submission: {} by user: {}", submissionId, userId);
            throw new InvalidRequestException("You don't have permission to access this submission");
        }

        return toDto(submission);
    }

    /**
     * Calculates XP earned based on score and difficulty
     * Easy: 1x multiplier, Medium: 1.5x, Hard: 2x
     */
    private int calculateXp(java.math.BigDecimal score, Question.Difficulty difficulty) {
        // Base XP from score (0-100)
        int baseXp = score.multiply(java.math.BigDecimal.valueOf(10)).intValue();

        // Difficulty multiplier
        double multiplier = switch (difficulty) {
            case EASY -> 1.0;
            case MEDIUM -> 1.5;
            case HARD -> 2.0;
            default -> 1.0;
        };

        return (int) (baseXp * multiplier);
    }

    /**
     * Updates user's streak based on last activity date
     */
    private void updateStreak(UserProgress progress) {
        LocalDate today = LocalDate.now();
        LocalDate lastActivity = progress.getLastActivityDate();

        if (lastActivity == null) {
            // First submission ever
            progress.setCurrentStreak(1);
            progress.setLongestStreak(1);
            log.debug("Started new streak");
        } else if (lastActivity.equals(today)) {
            // Already submitted today, no change to streak
            return;
        } else if (lastActivity.equals(today.minusDays(1))) {
            // Submitted yesterday, increment streak
            progress.setCurrentStreak(progress.getCurrentStreak() + 1);
            if (progress.getCurrentStreak() > progress.getLongestStreak()) {
                progress.setLongestStreak(progress.getCurrentStreak());
                log.debug("New longest streak: {}", progress.getLongestStreak());
            }
        } else {
            // Streak broken
            log.debug("Streak broken, resetting to 1");
            progress.setCurrentStreak(1);
        }

        progress.setLastActivityDate(today);
    }

    private SubmissionDto toDto(Submission submission) {
        return SubmissionDto.builder()
                .id(submission.getId())
                .questionId(submission.getQuestion().getId())
                .questionText(submission.getQuestion().getQuestionText())
                .originalImageUrl(submission.getOriginalImageUrl())
                .score(submission.getAiScore())
                .correct(submission.getIsCorrect())
                .feedback(submission.getAiFeedback())
                .correctSteps(submission.getCorrectSteps())
                .topicTags(submission.getTopicTags())
                .createdAt(submission.getCreatedAt())
                .build();
    }
}

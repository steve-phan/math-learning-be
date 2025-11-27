package com.mathlearning.controller;

import com.mathlearning.dto.ApiResponse;
import com.mathlearning.dto.SubmissionDto;
import com.mathlearning.dto.SubmissionResponse;
import com.mathlearning.service.ISubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/submissions")
@RequiredArgsConstructor
public class SubmissionController {

    private final ISubmissionService submissionService;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<SubmissionResponse>> uploadSubmission(
            @RequestParam("questionId") Long questionId,
            @RequestParam("image") MultipartFile image,
            Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();

        try {
            SubmissionResponse response = submissionService.createSubmission(userId, questionId, image);
            return ResponseEntity.ok(ApiResponse.success("Submission graded successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<SubmissionDto>>> getHistory(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();

        List<SubmissionDto> submissions = submissionService.getUserSubmissions(userId);
        return ResponseEntity.ok(ApiResponse.success(submissions));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SubmissionDto>> getSubmission(
            @PathVariable Long id,
            Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();

        try {
            SubmissionDto submission = submissionService.getSubmission(id, userId);
            return ResponseEntity.ok(ApiResponse.success(submission));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}

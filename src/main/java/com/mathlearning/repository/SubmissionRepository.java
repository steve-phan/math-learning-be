package com.mathlearning.repository;

import com.mathlearning.model.Submission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    List<Submission> findByUserIdOrderByCreatedAtDesc(Long userId);

    Page<Submission> findByUserId(Long userId, Pageable pageable);

    long countByUserIdAndIsCorrect(Long userId, Boolean isCorrect);
}

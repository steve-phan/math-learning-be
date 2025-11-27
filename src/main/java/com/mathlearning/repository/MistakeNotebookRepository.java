package com.mathlearning.repository;

import com.mathlearning.model.MistakeNotebook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MistakeNotebookRepository extends JpaRepository<MistakeNotebook, Long> {
    List<MistakeNotebook> findByUserIdAndReviewed(Long userId, Boolean reviewed);

    List<MistakeNotebook> findByUserId(Long userId);
}

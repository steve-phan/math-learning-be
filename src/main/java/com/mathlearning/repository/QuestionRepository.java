package com.mathlearning.repository;

import com.mathlearning.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByGradeLevelAndTopic(Integer gradeLevel, String topic);

    @Query("SELECT q FROM Question q WHERE q.gradeLevel = :gradeLevel ORDER BY RANDOM() LIMIT :limit")
    List<Question> findRandomQuestionsByGradeLevel(@Param("gradeLevel") Integer gradeLevel,
            @Param("limit") Integer limit);

    List<Question> findByGradeLevel(Integer gradeLevel);
}

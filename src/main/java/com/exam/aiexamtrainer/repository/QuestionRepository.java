package com.exam.aiexamtrainer.repository;

import com.exam.aiexamtrainer.entity.Question;
import com.exam.aiexamtrainer.enums.SourceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;

public interface QuestionRepository extends JpaRepository<Question, Long> {

  List<Question> findByActiveTrue();

  Optional<Question> findByIdAndActiveTrue(Long id);

  List<Question> findBySectionAndActiveTrue(String section);

  List<Question> findBySourceTypeAndActiveTrue(SourceType sourceType);

  List<Question> findBySectionAndSourceTypeAndActiveTrue(String section, SourceType sourceType);

  boolean existsByText(String text);

  @Query("select distinct q.section from Question q where q.active = true order by q.section")
  List<String> findDistinctActiveSections();
}
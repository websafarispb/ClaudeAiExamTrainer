package com.exam.aiexamtrainer.repository;

import com.exam.aiexamtrainer.entity.UserAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAnswerRepository extends JpaRepository<UserAnswer, Long> {

}
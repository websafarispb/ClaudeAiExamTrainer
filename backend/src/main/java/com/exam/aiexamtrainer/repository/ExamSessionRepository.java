package com.exam.aiexamtrainer.repository;

import com.exam.aiexamtrainer.entity.ExamSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamSessionRepository extends JpaRepository<ExamSession, Long> {

}
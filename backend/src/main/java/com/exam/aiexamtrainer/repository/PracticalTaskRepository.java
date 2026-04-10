package com.exam.aiexamtrainer.repository;

import com.exam.aiexamtrainer.entity.PracticalTask;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PracticalTaskRepository extends JpaRepository<PracticalTask, Long> {

  boolean existsByTitle(String title);
}
package com.exam.aiexamtrainer.entity;

import com.exam.aiexamtrainer.enums.ExamMode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "exam_sessions")
@Getter
@Setter
public class ExamSession {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ExamMode mode;

  @Column(nullable = false)
  private LocalDateTime startedAt;

  private LocalDateTime finishedAt;

  @Column(nullable = false)
  private Integer totalQuestions;

  private Integer correctAnswers = 0;

  @Column(nullable = false)
  private Boolean completed = false;

  @OneToMany(mappedBy = "examSession", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<UserAnswer> userAnswers = new ArrayList<>();
}

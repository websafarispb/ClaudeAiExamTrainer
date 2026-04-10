package com.exam.aiexamtrainer.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_answers")
@Getter
@Setter
public class UserAnswer {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "exam_session_id", nullable = false)
  private ExamSession examSession;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "question_id", nullable = false)
  private Question question;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "selected_option_id", nullable = false)
  private AnswerOption selectedOption;

  @Column(nullable = false)
  private Boolean correct;
}

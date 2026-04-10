package com.exam.aiexamtrainer.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "answer_options")
@Getter
@Setter
public class AnswerOption {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 1000)
  private String text;

  @Column(nullable = false)
  private Boolean correct;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "question_id", nullable = false)
  private Question question;
}

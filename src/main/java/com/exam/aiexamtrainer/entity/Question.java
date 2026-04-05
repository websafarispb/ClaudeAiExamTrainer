package com.exam.aiexamtrainer.entity;

import com.exam.aiexamtrainer.enums.DifficultyLevel;
import com.exam.aiexamtrainer.enums.QuestionType;
import com.exam.aiexamtrainer.enums.SourceType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "questions")
@Getter
@Setter
public class Question {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String section;

  @Column(nullable = false)
  private String topic;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private QuestionType type;

  @Column(nullable = false, length = 2000)
  private String text;

  @Column(length = 4000)
  private String explanation;

  @Column(length = 4000)
  private String translation;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private DifficultyLevel difficulty;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private SourceType sourceType;

  @Column(nullable = false)
  private Boolean active = true;

  @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<AnswerOption> options = new ArrayList<>();
}
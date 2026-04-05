package com.exam.aiexamtrainer.dto.ai;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenerateQuestionRequestDto {

  private String domain;
  private String difficulty;
  private String mode;
  private String baseQuestion;
}
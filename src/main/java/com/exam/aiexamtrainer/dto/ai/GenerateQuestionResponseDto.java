package com.exam.aiexamtrainer.dto.ai;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GenerateQuestionResponseDto {

  private String domain;
  private String difficulty;
  private String question;
  private List<GeneratedOptionDto> options;
  private String correctAnswer;
  private String explanation;
}

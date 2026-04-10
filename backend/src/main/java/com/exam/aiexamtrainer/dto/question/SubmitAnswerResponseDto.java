package com.exam.aiexamtrainer.dto.question;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubmitAnswerResponseDto {

  private Boolean correct;
  private String explanation;
  private String correctAnswer;
}
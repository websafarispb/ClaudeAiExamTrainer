package com.exam.aiexamtrainer.dto.ai;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TranslateQuestionRequestDto {

  private String provider;
  private GenerateQuestionResponseDto question;
}
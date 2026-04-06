package com.exam.aiexamtrainer.dto.ai;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TranslateQuestionResponseDto {

  private String question;
  private List<GeneratedOptionDto> options;
  private String explanation;
}

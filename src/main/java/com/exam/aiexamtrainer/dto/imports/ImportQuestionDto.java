package com.exam.aiexamtrainer.dto.imports;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ImportQuestionDto {

  private String section;
  private String topic;
  private String type;
  private String text;
  private String explanation;
  private String difficulty;
  private String sourceType;
  private List<ImportAnswerOptionDto> options;
}

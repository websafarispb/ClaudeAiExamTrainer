package com.exam.aiexamtrainer.dto.question;

import com.exam.aiexamtrainer.dto.AnswerOptionDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class QuestionResponseDto {

  private Long id;
  private String section;
  private String topic;
  private String text;
  private String difficulty;
  private List<AnswerOptionDto> options;
}

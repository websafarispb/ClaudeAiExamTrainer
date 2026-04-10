package com.exam.aiexamtrainer.dto.question;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubmitAnswerRequestDto {

  @NotNull
  private Long selectedOptionId;
}
package com.exam.aiexamtrainer.dto.ai;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TranslatePracticalTaskRequestDto {

  private String provider;
  private GeneratePracticalTaskResponseDto task;
}
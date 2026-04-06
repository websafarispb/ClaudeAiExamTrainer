package com.exam.aiexamtrainer.dto.ai;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GeneratePracticalTaskRequestDto {

  private String provider;
  private String domain;
  private String difficulty;
}

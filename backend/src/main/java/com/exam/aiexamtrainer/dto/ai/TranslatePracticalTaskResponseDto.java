package com.exam.aiexamtrainer.dto.ai;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TranslatePracticalTaskResponseDto {

  private String title;
  private String scenario;
  private String task;
  private List<String> whatToCover;
  private String sampleApproach;
}

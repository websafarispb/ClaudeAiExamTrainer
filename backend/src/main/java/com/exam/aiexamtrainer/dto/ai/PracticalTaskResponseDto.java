package com.exam.aiexamtrainer.dto.ai;

import lombok.Data;
import java.util.List;

@Data
public class PracticalTaskResponseDto {

  private Long id;

  private String title;
  private String scenario;
  private String task;

  private List<String> whatToCover;

  private String sampleApproach;
}
package com.exam.aiexamtrainer.controller;

import com.exam.aiexamtrainer.dto.ai.GeneratePracticalTaskRequestDto;
import com.exam.aiexamtrainer.dto.ai.GeneratePracticalTaskResponseDto;
import com.exam.aiexamtrainer.dto.ai.TranslateQuestionRequestDto;
import com.exam.aiexamtrainer.dto.ai.TranslateQuestionResponseDto;
import com.exam.aiexamtrainer.dto.question.QuestionResponseDto;
import com.exam.aiexamtrainer.dto.ai.GenerateQuestionRequestDto;
import com.exam.aiexamtrainer.dto.ai.GenerateQuestionResponseDto;
import com.exam.aiexamtrainer.service.AiQuestionGenerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

  private final AiQuestionGenerationService aiQuestionGenerationService;

  @PostMapping("/generate-question")
  public GenerateQuestionResponseDto generateQuestion(@RequestBody GenerateQuestionRequestDto request) {

    return aiQuestionGenerationService.generateQuestion(request);
  }

  @PostMapping("/save-generated-question")
  public QuestionResponseDto saveGeneratedQuestion(
      @RequestBody GenerateQuestionResponseDto generatedQuestion) {

    return aiQuestionGenerationService.saveGeneratedQuestion(generatedQuestion);
  }

  @PostMapping("/generate-and-save-question")
  public QuestionResponseDto generateAndSaveQuestion(@RequestBody GenerateQuestionRequestDto request) {

    return aiQuestionGenerationService.generateAndSaveQuestion(request);
  }

  @PostMapping("/generate-practical-task")
  public GeneratePracticalTaskResponseDto generatePracticalTask(
      @RequestBody GeneratePracticalTaskRequestDto request) {

    return aiQuestionGenerationService.generatePracticalTask(request);
  }

  @PostMapping("/translate-question")
  public TranslateQuestionResponseDto translateQuestion(
      @RequestBody TranslateQuestionRequestDto request) {

    return aiQuestionGenerationService.translateQuestion(request);
  }
}
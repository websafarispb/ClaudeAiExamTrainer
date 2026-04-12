package com.exam.aiexamtrainer.controller;

import com.exam.aiexamtrainer.config.AiSecurityProperties;
import com.exam.aiexamtrainer.dto.ai.GeneratePracticalTaskRequestDto;
import com.exam.aiexamtrainer.dto.ai.GeneratePracticalTaskResponseDto;
import com.exam.aiexamtrainer.dto.ai.PracticalTaskResponseDto;
import com.exam.aiexamtrainer.dto.ai.TranslatePracticalTaskRequestDto;
import com.exam.aiexamtrainer.dto.ai.TranslatePracticalTaskResponseDto;
import com.exam.aiexamtrainer.dto.ai.TranslateQuestionRequestDto;
import com.exam.aiexamtrainer.dto.ai.TranslateQuestionResponseDto;
import com.exam.aiexamtrainer.dto.question.QuestionResponseDto;
import com.exam.aiexamtrainer.dto.ai.GenerateQuestionRequestDto;
import com.exam.aiexamtrainer.dto.ai.GenerateQuestionResponseDto;
import com.exam.aiexamtrainer.service.AiQuestionGenerationService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

  private final AiQuestionGenerationService aiQuestionGenerationService;
  private final AiSecurityProperties aiSecurityProperties;

  @PostMapping("/generate-question")
  public GenerateQuestionResponseDto generateQuestion(
      @RequestBody GenerateQuestionRequestDto request,
      HttpServletRequest httpRequest) {

    ensureAiAccess(httpRequest);
    return aiQuestionGenerationService.generateQuestion(request);
  }

  @PostMapping("/save-generated-question")
  public QuestionResponseDto saveGeneratedQuestion(
      @RequestBody GenerateQuestionResponseDto generatedQuestion) {

    return aiQuestionGenerationService.saveGeneratedQuestion(generatedQuestion);
  }

  @PostMapping("/generate-and-save-question")
  public QuestionResponseDto generateAndSaveQuestion(
      @RequestBody GenerateQuestionRequestDto request,
      HttpServletRequest httpRequest) {

    ensureAiAccess(httpRequest);
    return aiQuestionGenerationService.generateAndSaveQuestion(request);
  }

  @PostMapping("/generate-practical-task")
  public GeneratePracticalTaskResponseDto generatePracticalTask(
      @RequestBody GeneratePracticalTaskRequestDto request,
      HttpServletRequest httpRequest) {

    ensureAiAccess(httpRequest);
    return aiQuestionGenerationService.generatePracticalTask(request);
  }

  @PostMapping("/translate-question")
  public TranslateQuestionResponseDto translateQuestion(
      @RequestBody TranslateQuestionRequestDto request,
      HttpServletRequest httpRequest) {

    ensureAiAccess(httpRequest);
    return aiQuestionGenerationService.translateQuestion(request);
  }

  @PostMapping("/translate-practical-task")
  public TranslatePracticalTaskResponseDto translatePracticalTask(
      @RequestBody TranslatePracticalTaskRequestDto request,
      HttpServletRequest httpRequest) {

    ensureAiAccess(httpRequest);
    return aiQuestionGenerationService.translatePracticalTask(request);
  }

  @PostMapping("/save-generated-practical-task")
  public PracticalTaskResponseDto saveGeneratedPracticalTask(
      @RequestBody GeneratePracticalTaskResponseDto task) {

    return aiQuestionGenerationService.saveGeneratedPracticalTask(task);
  }

  @GetMapping("/practical-tasks")
  public List<PracticalTaskResponseDto> getPracticalTasks() {

    return aiQuestionGenerationService.getAllPracticalTasks();
  }

  private void ensureAiAccess(HttpServletRequest request) {

    if (!aiSecurityProperties.enabled()) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "AI features are disabled");
    }

    if (aiSecurityProperties.requireAdminToken()) {
      String token = request.getHeader("X-Admin-Token");

      if (token == null || token.isBlank() || !token.equals(aiSecurityProperties.adminToken())) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin token is required");
      }
    }
  }
}
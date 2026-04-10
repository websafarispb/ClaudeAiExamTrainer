package com.exam.aiexamtrainer.controller;

import com.exam.aiexamtrainer.dto.question.QuestionResponseDto;
import com.exam.aiexamtrainer.dto.question.SubmitAnswerRequestDto;
import com.exam.aiexamtrainer.dto.question.SubmitAnswerResponseDto;
import com.exam.aiexamtrainer.service.QuestionService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

  private final QuestionService questionService;

  @GetMapping("/test")
  public List<QuestionResponseDto> getTestQuestions(
      @RequestParam int count,
      @RequestParam(required = false) String section,
      @RequestParam(required = false) String sourceType) {

    return questionService.getTestQuestions(count, section, sourceType);
  }

  @GetMapping("/random")
  public QuestionResponseDto getRandomQuestion(
      @RequestParam(required = false) String section,
      @RequestParam(required = false) String sourceType) {

    return questionService.getRandomQuestion(section, sourceType);
  }

  @GetMapping("/{id}")
  public QuestionResponseDto getQuestionById(@PathVariable Long id) {

    return questionService.getQuestionById(id);
  }

  @PostMapping("/{id}/submit-answer")
  public SubmitAnswerResponseDto submitAnswer(@PathVariable Long id,
      @Valid @RequestBody SubmitAnswerRequestDto request) {

    return questionService.submitAnswer(id, request.getSelectedOptionId());
  }

  @GetMapping("/sections")
  public List<String> getSections() {

    return questionService.getSections();
  }
}
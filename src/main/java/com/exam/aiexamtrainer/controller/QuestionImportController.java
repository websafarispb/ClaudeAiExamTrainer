package com.exam.aiexamtrainer.controller;

import com.exam.aiexamtrainer.service.QuestionImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionImportController {

  private final QuestionImportService questionImportService;

  @PostMapping("/import")
  public Map<String, Object> importQuestions() {

    int importedCount = questionImportService.importQuestions();

    return Map.of(
        "message", "Questions imported successfully",
        "importedCount", importedCount
    );
  }
}

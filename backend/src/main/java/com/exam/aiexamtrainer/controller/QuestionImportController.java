package com.exam.aiexamtrainer.controller;

import com.exam.aiexamtrainer.config.AiSecurityProperties;
import com.exam.aiexamtrainer.service.QuestionImportService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionImportController {

  private final QuestionImportService questionImportService;
  private final AiSecurityProperties aiSecurityProperties;

  @PostMapping("/import")
  public Map<String, Object> importQuestions(HttpServletRequest request) {

    ensureAdminAccess(request);

    int importedCount = questionImportService.importQuestions();

    return Map.of(
        "message", "Questions imported successfully",
        "importedCount", importedCount
    );
  }

  private void ensureAdminAccess(HttpServletRequest request) {

    if (aiSecurityProperties.requireAdminToken()) {
      String token = request.getHeader("X-Admin-Token");

      if (token == null || token.isBlank() || !token.equals(aiSecurityProperties.adminToken())) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin token is required");
      }
    }
  }
}
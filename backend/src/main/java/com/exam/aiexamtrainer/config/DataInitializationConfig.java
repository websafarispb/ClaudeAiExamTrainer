package com.exam.aiexamtrainer.config;

import com.exam.aiexamtrainer.repository.QuestionRepository;
import com.exam.aiexamtrainer.service.QuestionImportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataInitializationConfig {

  @Value("${app.questions.auto-import:true}")
  private boolean autoImport;

  private final QuestionRepository questionRepository;
  private final QuestionImportService questionImportService;

  @Bean
  public CommandLineRunner initQuestions() {

    return args -> {
      long count = questionRepository.count();

      if (autoImport && count == 0) {
        int importedCount = questionImportService.importQuestions();
        log.info("Imported {} questions on startup", importedCount);
      }
      else {
        log.info("Questions already exist ({}). Import skipped.", count);
      }
    };
  }
}

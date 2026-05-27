package com.exam.aiexamtrainer.service.impl;

import com.exam.aiexamtrainer.dto.imports.ImportAnswerOptionDto;
import com.exam.aiexamtrainer.dto.imports.ImportQuestionDto;
import com.exam.aiexamtrainer.entity.AnswerOption;
import com.exam.aiexamtrainer.entity.Question;
import com.exam.aiexamtrainer.enums.DifficultyLevel;
import com.exam.aiexamtrainer.enums.QuestionType;
import com.exam.aiexamtrainer.enums.SourceType;
import com.exam.aiexamtrainer.repository.QuestionRepository;
import com.exam.aiexamtrainer.service.QuestionImportService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuestionImportServiceImpl implements QuestionImportService {

  private final ObjectMapper objectMapper;
  private final QuestionRepository questionRepository;

  @Transactional
  @Override
  public int importQuestions() {

    List<String> files = List.of(
        "questions/1.json",
        "questions/2.json",
        "questions/3.json",
        "questions/4.json"
    );

    int importedCount = 0;

    for (String file : files) {
      importedCount += importFromFile(file);
    }

    return importedCount;
  }

  private int importFromFile(String filePath) {

    try {
      ClassPathResource resource = new ClassPathResource(filePath);
      InputStream inputStream = resource.getInputStream();

      List<ImportQuestionDto> questions = objectMapper.readValue(
          inputStream,
          new TypeReference<List<ImportQuestionDto>>() {
          }
      );

      int count = 0;

      for (ImportQuestionDto dto : questions) {
        if (questionRepository.existsByText(dto.getText())) {
          continue;
        }

        Question question = mapToEntity(dto);
        questionRepository.save(question);
        count++;
      }

      return count;
    }
    catch (Exception e) {
      throw new RuntimeException("Failed to import questions from file: " + filePath, e);
    }
  }

  private Question mapToEntity(ImportQuestionDto dto) {

    Question question = new Question();
    question.setSection(dto.getSection());
    question.setTopic(dto.getTopic());
    question.setType(QuestionType.valueOf(dto.getType()
        .trim()
        .toUpperCase()));
    question.setText(dto.getText());
    question.setExplanation(dto.getExplanation());
    question.setTranslation(null);
    question.setDifficulty(DifficultyLevel.valueOf(dto.getDifficulty()
        .trim()
        .toUpperCase()));
    question.setSourceType(SourceType.valueOf(dto.getSourceType()
        .trim()
        .toUpperCase()));
    question.setActive(true);
    question.setOptions(new ArrayList<>());

    for (ImportAnswerOptionDto optionDto : dto.getOptions()) {
      AnswerOption option = new AnswerOption();
      option.setText(optionDto.getText());
      option.setCorrect(Boolean.TRUE.equals(optionDto.getCorrect()));
      option.setQuestion(question);
      question.getOptions()
          .add(option);
    }

    return question;
  }
}
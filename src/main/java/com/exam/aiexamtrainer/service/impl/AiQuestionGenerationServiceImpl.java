package com.exam.aiexamtrainer.service.impl;

import com.exam.aiexamtrainer.dto.question.QuestionResponseDto;
import com.exam.aiexamtrainer.dto.ai.GenerateQuestionRequestDto;
import com.exam.aiexamtrainer.dto.ai.GenerateQuestionResponseDto;
import com.exam.aiexamtrainer.entity.Question;
import com.exam.aiexamtrainer.exception.BadRequestException;
import com.exam.aiexamtrainer.mapper.AiGeneratedQuestionMapper;
import com.exam.aiexamtrainer.mapper.QuestionMapper;
import com.exam.aiexamtrainer.repository.QuestionRepository;
import com.exam.aiexamtrainer.service.AiQuestionGenerationService;
import com.exam.aiexamtrainer.service.LlmClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiQuestionGenerationServiceImpl implements AiQuestionGenerationService {

  private final LlmClient llmClient;
  private final AiPromptBuilder aiPromptBuilder;
  private final ObjectMapper objectMapper;
  private final AiGeneratedQuestionMapper aiGeneratedQuestionMapper;
  private final QuestionRepository questionRepository;
  private final QuestionMapper questionMapper;

  @Override
  public GenerateQuestionResponseDto generateQuestion(GenerateQuestionRequestDto request) {

    String systemPrompt = aiPromptBuilder.buildSystemPrompt();
    String userPrompt = aiPromptBuilder.buildUserPrompt(request);

    String rawResponse = llmClient.generate(systemPrompt, userPrompt);

    try {
      GenerateQuestionResponseDto response =
          objectMapper.readValue(rawResponse, GenerateQuestionResponseDto.class);

      validateResponse(response);

      return response;
    }
    catch (Exception e) {
      throw new BadRequestException("Failed to parse AI response");
    }
  }

  @Override
  public QuestionResponseDto generateAndSaveQuestion(GenerateQuestionRequestDto request) {

    GenerateQuestionResponseDto generatedQuestion = generateQuestion(request);
    Question question = aiGeneratedQuestionMapper.toEntity(generatedQuestion);
    Question savedQuestion = questionRepository.save(question);
    return questionMapper.toDto(savedQuestion);
  }

  private void validateResponse(GenerateQuestionResponseDto response) {

    if (response.getDomain() == null || response.getDomain()
        .isBlank()) {
      throw new BadRequestException("AI response missing domain");
    }

    if (response.getDifficulty() == null || response.getDifficulty()
        .isBlank()) {
      throw new BadRequestException("AI response missing difficulty");
    }

    if (response.getQuestion() == null || response.getQuestion()
        .isBlank()) {
      throw new BadRequestException("AI response missing question");
    }

    if (response.getOptions() == null || response.getOptions()
        .size() != 4) {
      throw new BadRequestException("AI response must contain exactly 4 options");
    }

    String letters = response.getOptions()
        .stream()
        .map(option -> option.getLetter() == null ? "" : option.getLetter()
            .trim()
            .toUpperCase())
        .sorted()
        .reduce("", String::concat);

    if (!"ABCD".equals(letters)) {
      throw new BadRequestException("AI response options must use letters A, B, C, D");
    }

    String correctAnswer = response.getCorrectAnswer() == null
        ? ""
        : response.getCorrectAnswer()
            .trim()
            .toUpperCase();

    if (!correctAnswer.matches("[ABCD]")) {
      throw new BadRequestException("AI response correctAnswer must be one of A, B, C, D");
    }

    if (response.getExplanation() == null || response.getExplanation()
        .isBlank()) {
      throw new BadRequestException("AI response missing explanation");
    }
  }
}
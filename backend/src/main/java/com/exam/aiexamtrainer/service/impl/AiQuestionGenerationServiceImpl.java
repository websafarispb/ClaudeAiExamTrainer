package com.exam.aiexamtrainer.service.impl;

import com.exam.aiexamtrainer.dto.ai.GeneratePracticalTaskRequestDto;
import com.exam.aiexamtrainer.dto.ai.GeneratePracticalTaskResponseDto;
import com.exam.aiexamtrainer.dto.ai.GenerateQuestionRequestDto;
import com.exam.aiexamtrainer.dto.ai.GenerateQuestionResponseDto;
import com.exam.aiexamtrainer.dto.ai.PracticalTaskResponseDto;
import com.exam.aiexamtrainer.dto.ai.TranslatePracticalTaskRequestDto;
import com.exam.aiexamtrainer.dto.ai.TranslatePracticalTaskResponseDto;
import com.exam.aiexamtrainer.dto.ai.TranslateQuestionRequestDto;
import com.exam.aiexamtrainer.dto.ai.TranslateQuestionResponseDto;
import com.exam.aiexamtrainer.dto.question.QuestionResponseDto;
import com.exam.aiexamtrainer.entity.PracticalTask;
import com.exam.aiexamtrainer.entity.Question;
import com.exam.aiexamtrainer.enums.LlmProvider;
import com.exam.aiexamtrainer.exception.BadRequestException;
import com.exam.aiexamtrainer.exception.DuplicateQuestionException;
import com.exam.aiexamtrainer.mapper.AiGeneratedQuestionMapper;
import com.exam.aiexamtrainer.mapper.QuestionMapper;
import com.exam.aiexamtrainer.repository.PracticalTaskRepository;
import com.exam.aiexamtrainer.repository.QuestionRepository;
import com.exam.aiexamtrainer.service.AiQuestionGenerationService;
import com.exam.aiexamtrainer.service.LlmClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiQuestionGenerationServiceImpl implements AiQuestionGenerationService {

  private final List<LlmClient> llmClients;
  private final AiPromptBuilder aiPromptBuilder;
  private final ObjectMapper objectMapper;
  private final AiGeneratedQuestionMapper aiGeneratedQuestionMapper;
  private final QuestionRepository questionRepository;
  private final PracticalTaskRepository practicalTaskRepository;
  private final QuestionMapper questionMapper;

  @Override
  public GenerateQuestionResponseDto generateQuestion(GenerateQuestionRequestDto request) {

    String systemPrompt = aiPromptBuilder.buildSystemPrompt();
    String userPrompt = aiPromptBuilder.buildUserPrompt(request);

    LlmClient client = resolveClient(request.getProvider());
    String rawResponse = client.generate(systemPrompt, userPrompt);

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

  @Override
  public GeneratePracticalTaskResponseDto generatePracticalTask(GeneratePracticalTaskRequestDto request) {

    String systemPrompt = aiPromptBuilder.buildPracticalTaskSystemPrompt();
    String userPrompt = aiPromptBuilder.buildPracticalTaskUserPrompt(request);

    LlmClient client = resolveClient(request.getProvider());
    String rawResponse = client.generate(systemPrompt, userPrompt);

    try {
      GeneratePracticalTaskResponseDto response =
          objectMapper.readValue(rawResponse, GeneratePracticalTaskResponseDto.class);

      validatePracticalTaskResponse(response);

      return response;
    }
    catch (Exception e) {
      throw new BadRequestException("Failed to parse AI practical task response");
    }
  }

  @Override
  public QuestionResponseDto saveGeneratedQuestion(GenerateQuestionResponseDto generatedQuestion) {

    validateResponse(generatedQuestion);

    if (questionRepository.existsByText(generatedQuestion.getQuestion())) {
      throw new DuplicateQuestionException("This question already exists in the question bank");
    }

    Question question = aiGeneratedQuestionMapper.toEntity(generatedQuestion);
    Question savedQuestion = questionRepository.save(question);

    return questionMapper.toDto(savedQuestion);
  }

  @Override
  public TranslateQuestionResponseDto translateQuestion(TranslateQuestionRequestDto request) {

    if (request == null || request.getQuestion() == null) {
      throw new BadRequestException("Question is required for translation");
    }

    String systemPrompt = aiPromptBuilder.buildTranslateQuestionSystemPrompt();
    String userPrompt = aiPromptBuilder.buildTranslateQuestionUserPrompt(request.getQuestion());

    LlmClient client = resolveClient(request.getProvider());
    String rawResponse = client.generate(systemPrompt, userPrompt);

    try {
      TranslateQuestionResponseDto response =
          objectMapper.readValue(rawResponse, TranslateQuestionResponseDto.class);

      validateTranslatedQuestionResponse(response);

      return response;
    }
    catch (Exception e) {
      throw new BadRequestException("Failed to parse translated question response");
    }
  }

  @Override
  public TranslatePracticalTaskResponseDto translatePracticalTask(TranslatePracticalTaskRequestDto request) {

    if (request == null || request.getTask() == null) {
      throw new BadRequestException("Practical task is required for translation");
    }

    String systemPrompt = aiPromptBuilder.buildTranslatePracticalTaskSystemPrompt();
    String userPrompt = aiPromptBuilder.buildTranslatePracticalTaskUserPrompt(request.getTask());

    LlmClient client = resolveClient(request.getProvider());
    String rawResponse = client.generate(systemPrompt, userPrompt);

    try {
      log.debug("RAW PRACTICAL TASK TRANSLATION RESPONSE:");
      log.debug(rawResponse);
      TranslatePracticalTaskResponseDto response =
          objectMapper.readValue(rawResponse, TranslatePracticalTaskResponseDto.class);

      validateTranslatedPracticalTaskResponse(response);

      return response;
    }
    catch (Exception e) {
      throw new BadRequestException("Failed to parse translated practical task response");
    }
  }

  @Override
  public PracticalTaskResponseDto saveGeneratedPracticalTask(GeneratePracticalTaskResponseDto taskDto) {

    if (taskDto == null) {
      throw new BadRequestException("Task is required");
    }

    if (practicalTaskRepository.existsByTitle(taskDto.getTitle())) {
      throw new DuplicateQuestionException("This practical task already exists");
    }

    PracticalTask entity = new PracticalTask();
    entity.setTitle(taskDto.getTitle());
    entity.setScenario(taskDto.getScenario());
    entity.setTask(taskDto.getTask());
    entity.setWhatToCover(taskDto.getWhatToCover());
    entity.setSampleApproach(taskDto.getSampleApproach());

    PracticalTask saved = practicalTaskRepository.save(entity);

    return mapToDto(saved);
  }

  @Override
  public List<PracticalTaskResponseDto> getAllPracticalTasks() {

    return practicalTaskRepository.findAll()
        .stream()
        .map(this::mapToDto)
        .toList();
  }

  private PracticalTaskResponseDto mapToDto(PracticalTask entity) {

    PracticalTaskResponseDto dto = new PracticalTaskResponseDto();

    dto.setId(entity.getId());
    dto.setTitle(entity.getTitle());
    dto.setScenario(entity.getScenario());
    dto.setTask(entity.getTask());
    dto.setWhatToCover(entity.getWhatToCover());
    dto.setSampleApproach(entity.getSampleApproach());

    return dto;
  }

  private void validateTranslatedPracticalTaskResponse(TranslatePracticalTaskResponseDto response) {

    if (response.getTitle() == null || response.getTitle()
        .isBlank()) {
      throw new BadRequestException("Translated practical task title is missing");
    }

    if (response.getScenario() == null || response.getScenario()
        .isBlank()) {
      throw new BadRequestException("Translated practical task scenario is missing");
    }

    if (response.getTask() == null || response.getTask()
        .isBlank()) {
      throw new BadRequestException("Translated practical task text is missing");
    }

    if (response.getWhatToCover() == null || response.getWhatToCover()
        .isEmpty()) {
      throw new BadRequestException("Translated practical task whatToCover is missing");
    }

    if (response.getSampleApproach() == null || response.getSampleApproach()
        .isBlank()) {
      throw new BadRequestException("Translated practical task sampleApproach is missing");
    }
  }

  private void validateTranslatedQuestionResponse(TranslateQuestionResponseDto response) {

    if (response.getQuestion() == null || response.getQuestion()
        .isBlank()) {
      throw new BadRequestException("Translated question is missing");
    }

    if (response.getOptions() == null || response.getOptions()
        .size() != 4) {
      throw new BadRequestException("Translated question must contain exactly 4 options");
    }
  }

  private void validatePracticalTaskResponse(GeneratePracticalTaskResponseDto response) {

    if (response.getDomain() == null || response.getDomain()
        .isBlank()) {
      throw new BadRequestException("Practical task response missing domain");
    }

    if (response.getDifficulty() == null || response.getDifficulty()
        .isBlank()) {
      throw new BadRequestException("Practical task response missing difficulty");
    }

    if (response.getTitle() == null || response.getTitle()
        .isBlank()) {
      throw new BadRequestException("Practical task response missing title");
    }

    if (response.getScenario() == null || response.getScenario()
        .isBlank()) {
      throw new BadRequestException("Practical task response missing scenario");
    }

    if (response.getTask() == null || response.getTask()
        .isBlank()) {
      throw new BadRequestException("Practical task response missing task");
    }

    if (response.getWhatToCover() == null || response.getWhatToCover()
        .isEmpty()) {
      throw new BadRequestException("Practical task response missing whatToCover");
    }

    if (response.getSampleApproach() == null || response.getSampleApproach()
        .isBlank()) {
      throw new BadRequestException("Practical task response missing sampleApproach");
    }
  }

  private LlmClient resolveClient(String providerValue) {

    LlmProvider provider;

    try {
      provider = providerValue == null || providerValue.isBlank()
          ? LlmProvider.CLAUDE
          : LlmProvider.valueOf(providerValue.trim()
              .toUpperCase());
    }
    catch (IllegalArgumentException e) {
      throw new BadRequestException("Unsupported provider: " + providerValue);
    }

    return llmClients.stream()
        .filter(client -> client.getProvider() == provider)
        .findFirst()
        .orElseThrow(() -> new BadRequestException("No client configured for provider: " + provider));
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
package com.exam.aiexamtrainer.service.impl;

import com.exam.aiexamtrainer.dto.question.QuestionResponseDto;
import com.exam.aiexamtrainer.dto.question.SubmitAnswerResponseDto;
import com.exam.aiexamtrainer.entity.AnswerOption;
import com.exam.aiexamtrainer.entity.Question;
import com.exam.aiexamtrainer.enums.SourceType;
import com.exam.aiexamtrainer.exception.BadRequestException;
import com.exam.aiexamtrainer.exception.NotFoundException;
import com.exam.aiexamtrainer.mapper.QuestionMapper;
import com.exam.aiexamtrainer.repository.QuestionRepository;
import com.exam.aiexamtrainer.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

  private final QuestionRepository questionRepository;
  private final QuestionMapper questionMapper;
  private final Random random = new Random();

  @Override
  public QuestionResponseDto getRandomQuestion(String section, String sourceType) {

    List<Question> questions = findQuestions(section, sourceType);

    if (questions.isEmpty()) {
      throw new NotFoundException("No questions found for provided filters");
    }

    Question randomQuestion = questions.get(random.nextInt(questions.size()));
    return questionMapper.toDto(randomQuestion);
  }

  @Override
  public QuestionResponseDto getQuestionById(Long id) {

    Question question = questionRepository.findByIdAndActiveTrue(id)
        .orElseThrow(() -> new NotFoundException("Question not found with id: " + id));

    return questionMapper.toDto(question);
  }

  @Override
  public SubmitAnswerResponseDto submitAnswer(Long questionId, Long selectedOptionId) {

    Question question = questionRepository.findByIdAndActiveTrue(questionId)
        .orElseThrow(() -> new NotFoundException("Question not found with id: " + questionId));

    AnswerOption selectedOption = question.getOptions()
        .stream()
        .filter(option -> option.getId()
            .equals(selectedOptionId))
        .findFirst()
        .orElseThrow(() -> new BadRequestException("Selected option does not belong to this question"));

    AnswerOption correctOption = question.getOptions()
        .stream()
        .filter(option -> Boolean.TRUE.equals(option.getCorrect()))
        .findFirst()
        .orElseThrow(() -> new BadRequestException("Correct answer is not configured for question id: " + questionId));

    SubmitAnswerResponseDto response = new SubmitAnswerResponseDto();
    response.setCorrect(selectedOption.getCorrect());
    response.setExplanation(question.getExplanation());
    response.setCorrectAnswer(correctOption.getText());

    return response;
  }

  @Override
  public List<String> getSections() {

    return questionRepository.findDistinctActiveSections();
  }

  private List<Question> findQuestions(String section, String sourceType) {

    boolean hasSection = section != null && !section.isBlank();
    boolean hasSourceType = sourceType != null && !sourceType.isBlank();

    if (hasSection && hasSourceType) {
      return questionRepository.findBySectionAndSourceTypeAndActiveTrue(
          section,
          parseSourceType(sourceType)
      );
    }

    if (hasSection) {
      return questionRepository.findBySectionAndActiveTrue(section);
    }

    if (hasSourceType) {
      return questionRepository.findBySourceTypeAndActiveTrue(parseSourceType(sourceType));
    }

    return questionRepository.findByActiveTrue();
  }

  private SourceType parseSourceType(String sourceType) {

    try {
      return SourceType.valueOf(sourceType.trim()
          .toUpperCase());
    }
    catch (IllegalArgumentException e) {
      throw new BadRequestException("Unsupported sourceType: " + sourceType);
    }
  }
}
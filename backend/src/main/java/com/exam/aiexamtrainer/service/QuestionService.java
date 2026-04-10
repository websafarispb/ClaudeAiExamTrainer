package com.exam.aiexamtrainer.service;

import com.exam.aiexamtrainer.dto.question.QuestionResponseDto;
import com.exam.aiexamtrainer.dto.question.SubmitAnswerResponseDto;
import java.util.List;

public interface QuestionService {

  QuestionResponseDto getRandomQuestion(String section, String sourceType);

  List<QuestionResponseDto> getTestQuestions(int count, String section, String sourceType);

  QuestionResponseDto getQuestionById(Long id);

  SubmitAnswerResponseDto submitAnswer(Long questionId, Long selectedOptionId);

  List<String> getSections();
}
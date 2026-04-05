package com.exam.aiexamtrainer.service;

import com.exam.aiexamtrainer.dto.question.QuestionResponseDto;
import com.exam.aiexamtrainer.dto.ai.GenerateQuestionRequestDto;
import com.exam.aiexamtrainer.dto.ai.GenerateQuestionResponseDto;

public interface AiQuestionGenerationService {

  GenerateQuestionResponseDto generateQuestion(GenerateQuestionRequestDto request);

  QuestionResponseDto generateAndSaveQuestion(GenerateQuestionRequestDto request);
}
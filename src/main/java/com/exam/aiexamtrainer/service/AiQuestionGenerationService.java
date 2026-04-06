package com.exam.aiexamtrainer.service;

import com.exam.aiexamtrainer.dto.ai.GeneratePracticalTaskRequestDto;
import com.exam.aiexamtrainer.dto.ai.GeneratePracticalTaskResponseDto;
import com.exam.aiexamtrainer.dto.ai.TranslateQuestionRequestDto;
import com.exam.aiexamtrainer.dto.ai.TranslateQuestionResponseDto;
import com.exam.aiexamtrainer.dto.question.QuestionResponseDto;
import com.exam.aiexamtrainer.dto.ai.GenerateQuestionRequestDto;
import com.exam.aiexamtrainer.dto.ai.GenerateQuestionResponseDto;

public interface AiQuestionGenerationService {

  GenerateQuestionResponseDto generateQuestion(GenerateQuestionRequestDto request);

  QuestionResponseDto generateAndSaveQuestion(GenerateQuestionRequestDto request);

  GeneratePracticalTaskResponseDto generatePracticalTask(GeneratePracticalTaskRequestDto request);

  QuestionResponseDto saveGeneratedQuestion(GenerateQuestionResponseDto generatedQuestion);

  TranslateQuestionResponseDto translateQuestion(TranslateQuestionRequestDto request);
}
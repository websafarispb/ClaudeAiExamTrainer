package com.exam.aiexamtrainer.mapper;

import com.exam.aiexamtrainer.dto.ai.GenerateQuestionResponseDto;
import com.exam.aiexamtrainer.dto.ai.GeneratedOptionDto;
import com.exam.aiexamtrainer.entity.AnswerOption;
import com.exam.aiexamtrainer.entity.Question;
import com.exam.aiexamtrainer.enums.DifficultyLevel;
import com.exam.aiexamtrainer.enums.QuestionType;
import com.exam.aiexamtrainer.enums.SourceType;
import org.springframework.stereotype.Component;

@Component
public class AiGeneratedQuestionMapper {

  public Question toEntity(GenerateQuestionResponseDto dto) {

    Question question = new Question();
    question.setSection(dto.getDomain());
    question.setTopic(dto.getDomain());
    question.setType(QuestionType.SINGLE_CHOICE);
    question.setText(dto.getQuestion());
    question.setExplanation(dto.getExplanation());
    question.setTranslation(null);
    question.setDifficulty(mapDifficulty(dto.getDifficulty()));
    question.setSourceType(SourceType.AI_GENERATED);
    question.setActive(true);

    if (dto.getOptions() != null) {
      for (GeneratedOptionDto optionDto : dto.getOptions()) {
        AnswerOption option = new AnswerOption();
        option.setText(optionDto.getText());
        option.setCorrect(isCorrectOption(optionDto.getLetter(), dto.getCorrectAnswer()));
        option.setQuestion(question);
        question.getOptions()
            .add(option);
      }
    }

    return question;
  }

  private DifficultyLevel mapDifficulty(String difficulty) {

    return DifficultyLevel.valueOf(difficulty.trim()
        .toUpperCase());
  }

  private boolean isCorrectOption(String optionLetter, String correctAnswer) {

    return optionLetter != null
        && correctAnswer != null
        && optionLetter.trim()
        .equalsIgnoreCase(correctAnswer.trim());
  }
}
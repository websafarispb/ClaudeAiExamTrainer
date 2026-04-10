package com.exam.aiexamtrainer.mapper;

import com.exam.aiexamtrainer.dto.AnswerOptionDto;
import com.exam.aiexamtrainer.dto.question.QuestionResponseDto;
import com.exam.aiexamtrainer.entity.AnswerOption;
import com.exam.aiexamtrainer.entity.Question;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class QuestionMapper {

  public QuestionResponseDto toDto(Question question) {

    QuestionResponseDto dto = new QuestionResponseDto();
    dto.setId(question.getId());
    dto.setSection(question.getSection());
    dto.setTopic(question.getTopic());
    dto.setText(question.getText());
    dto.setDifficulty(question.getDifficulty()
        .name());
    dto.setOptions(mapOptions(question.getOptions()));
    return dto;
  }

  private List<AnswerOptionDto> mapOptions(List<AnswerOption> options) {

    return options.stream()
        .map(this::mapOption)
        .toList();
  }

  private AnswerOptionDto mapOption(AnswerOption option) {

    AnswerOptionDto dto = new AnswerOptionDto();
    dto.setId(option.getId());
    dto.setText(option.getText());
    return dto;
  }
}

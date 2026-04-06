package com.exam.aiexamtrainer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(NotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public Map<String, Object> handleNotFound(NotFoundException ex) {

    return Map.of(
        "timestamp", LocalDateTime.now()
            .toString(),
        "status", 404,
        "error", "Not Found",
        "message", ex.getMessage()
    );
  }

  @ExceptionHandler(BadRequestException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, Object> handleBadRequest(BadRequestException ex) {

    return Map.of(
        "timestamp", LocalDateTime.now()
            .toString(),
        "status", 400,
        "error", "Bad Request",
        "message", ex.getMessage()
    );
  }

  @ExceptionHandler(DuplicateQuestionException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public Map<String, Object> handleDuplicateQuestion(DuplicateQuestionException ex) {

    return Map.of(
        "timestamp", LocalDateTime.now()
            .toString(),
        "status", 409,
        "error", "Conflict",
        "message", ex.getMessage()
    );
  }
}

package com.exam.aiexamtrainer.exception;

import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(NotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public Map<String, Object> handleNotFound(NotFoundException ex) {

    return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
  }

  @ExceptionHandler(BadRequestException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, Object> handleBadRequest(BadRequestException ex) {

    return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
  }

  @ExceptionHandler(DuplicateQuestionException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public Map<String, Object> handleDuplicateQuestion(DuplicateQuestionException ex) {

    return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
  }

  @ExceptionHandler(ResponseStatusException.class)
  @ResponseStatus
  public Map<String, Object> handleResponseStatus(ResponseStatusException ex) {

    HttpStatus status = HttpStatus.valueOf(ex.getStatusCode()
        .value());
    return buildErrorResponse(status, ex.getReason());
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public Map<String, Object> handleUnexpected(Exception ex) {

    return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error");
  }

  private Map<String, Object> buildErrorResponse(HttpStatus status, String message) {

    return Map.of(
        "timestamp", LocalDateTime.now()
            .toString(),
        "status", status.value(),
        "error", status.getReasonPhrase(),
        "message", message == null ? status.getReasonPhrase() : message
    );
  }
}

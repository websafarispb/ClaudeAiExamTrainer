package com.exam.aiexamtrainer.service.impl;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SessionQuotaService {

  private static final String QUESTION_COUNT_ATTR = "QUESTION_AI_COUNT";
  private static final String TASK_COUNT_ATTR = "TASK_AI_COUNT";

  private static final int QUESTION_LIMIT = 5;
  private static final int TASK_LIMIT = 3;

  public void checkAndIncrementQuestionQuota(HttpSession session) {

    int current = getInt(session, QUESTION_COUNT_ATTR);

    if (current >= QUESTION_LIMIT) {
      throw new ResponseStatusException(
          HttpStatus.TOO_MANY_REQUESTS,
          "Question AI limit reached for this session (5)");
    }

    session.setAttribute(QUESTION_COUNT_ATTR, current + 1);
  }

  public void checkAndIncrementTaskQuota(HttpSession session) {

    int current = getInt(session, TASK_COUNT_ATTR);

    if (current >= TASK_LIMIT) {
      throw new ResponseStatusException(
          HttpStatus.TOO_MANY_REQUESTS,
          "Practical task AI limit reached for this session (3)");
    }

    session.setAttribute(TASK_COUNT_ATTR, current + 1);
  }

  public int getRemainingQuestionQuota(HttpSession session) {

    return Math.max(0, QUESTION_LIMIT - getInt(session, QUESTION_COUNT_ATTR));
  }

  public int getRemainingTaskQuota(HttpSession session) {

    return Math.max(0, TASK_LIMIT - getInt(session, TASK_COUNT_ATTR));
  }

  private int getInt(HttpSession session, String key) {

    Object value = session.getAttribute(key);
    if (value instanceof Integer integer) {
      return integer;
    }
    return 0;
  }
}
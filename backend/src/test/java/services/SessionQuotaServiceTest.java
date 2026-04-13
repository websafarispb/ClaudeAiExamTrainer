package services;

import com.exam.aiexamtrainer.service.impl.SessionQuotaService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SessionQuotaServiceTest {

  private SessionQuotaService sessionQuotaService;
  private HttpSession session;

  @BeforeEach
  void setUp() {

    sessionQuotaService = new SessionQuotaService();
    session = mock(HttpSession.class);
  }

  @Test
  void shouldIncrementQuestionQuota() {

    when(session.getAttribute("QUESTION_AI_COUNT")).thenReturn(null);

    sessionQuotaService.checkAndIncrementQuestionQuota(session);

    verify(session).setAttribute("QUESTION_AI_COUNT", 1);
  }

  @Test
  void shouldThrowWhenQuestionQuotaExceeded() {

    when(session.getAttribute("QUESTION_AI_COUNT")).thenReturn(5);

    ResponseStatusException ex = assertThrows(
        ResponseStatusException.class,
        () -> sessionQuotaService.checkAndIncrementQuestionQuota(session)
    );

    assertEquals(429, ex.getStatusCode()
        .value());
    assertTrue(ex.getReason()
        .contains("Question AI limit reached"));
  }

  @Test
  void shouldIncrementTaskQuota() {

    when(session.getAttribute("TASK_AI_COUNT")).thenReturn(null);

    sessionQuotaService.checkAndIncrementTaskQuota(session);

    verify(session).setAttribute("TASK_AI_COUNT", 1);
  }

  @Test
  void shouldThrowWhenTaskQuotaExceeded() {

    when(session.getAttribute("TASK_AI_COUNT")).thenReturn(3);

    ResponseStatusException ex = assertThrows(
        ResponseStatusException.class,
        () -> sessionQuotaService.checkAndIncrementTaskQuota(session)
    );

    assertEquals(429, ex.getStatusCode()
        .value());
    assertTrue(ex.getReason()
        .contains("Practical task AI limit reached"));
  }
}
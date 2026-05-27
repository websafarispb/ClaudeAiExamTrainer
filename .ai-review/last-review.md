# AI Exam Trainer — Follow-Up Engineering Review

**Date:** 2026-05-27
**Previous review:** 2026-05-20 (`.ai-review/review-2026-05-20-initial.md`)
**Reviewer role:** Senior Java/Spring Boot, React, Docker, CI/CD engineer
**Branch reviewed:** `feature/ai-reviewer-practice`
**Scope:** Follow-up only — checking resolution of 21 previous findings, adding new findings

---

## Summary of Changes Since Previous Review

Twelve commits were pushed since the initial review, addressing the majority of the highest-severity findings:

| Commit | Description |
|--------|-------------|
| `0589fe7` | Fix database schema management for practical tasks |
| `f99f5b9` | Protect admin write endpoints with token check |
| `5ed2132` | Remove debug output and harden local config |
| `047822b` | Include OpenAI API key in dev deployment env |
| `7c78bde` | Use shared RestClient for OpenAI client |
| `6341dec` | Make question import transactional |
| `3a309b1` | Add environment variables example |
| `ef404cb` | Use npm ci in frontend Docker build |
| `05a5f92` | Ignore frontend build artifacts |
| `e6f7c94` | Normalize API error responses |
| `b90ef78` | Fix frontend Docker build |
| `5f4d9ce` | Add SessionQuotaService unit tests |

**10 of 21 findings are fully resolved.** Two findings were partially fixed but introduced new bugs. Two new HIGH findings were discovered — one as a direct result of a broken fix.

---

## Resolved Findings

- **REVIEW-01** ✅ `V2__add_practical_tasks.sql` now exists with correct DDL for `practical_task` and `practical_task_what_to_cover`. `ddl-auto` changed to `validate`. Flyway is now the sole schema authority.

- **REVIEW-02** ✅ `saveGeneratedQuestion` and `saveGeneratedPracticalTask` both call `ensureAiAccess(httpRequest)` at the top of the method. Access control is now consistently applied across all write endpoints in `AiController`.

- **REVIEW-03** ✅ `QuestionImportController` now calls `ensureAdminAccess(request)` which checks the `X-Admin-Token` when `requireAdminToken=true`. The import endpoint is protected behind the same mechanism as the AI controller.

- **REVIEW-05** ✅ `jpa.show-sql` is now `false` in `application.yaml`. SQL logging is suppressed in all environments.

- **REVIEW-06** ✅ `spring.h2.console.enabled` is now `false` in `application.yaml`. The H2 console endpoint is no longer registered in any profile.

- **REVIEW-09** ✅ `QuestionImportServiceImpl.importQuestions()` is now annotated `@Transactional`. A failure mid-import will roll back all previously saved questions in that run.

- **REVIEW-10** ✅ `OpenAiLlmClient` no longer creates its own `RestClient` instance. The shared `RestClient` bean is now injected via `@RequiredArgsConstructor`, consistent with `ClaudeApiLlmClient`.

- **REVIEW-15** ✅ `frontend/dist/` is in `.gitignore` and has been removed from git tracking (`git ls-files frontend/dist/` returns empty). The repository no longer carries stale build artifacts.

- **REVIEW-16** ✅ `frontend/Dockerfile` now uses `npm ci` instead of `npm install`. Docker builds are reproducible and consistent with what CI tests.

- **REVIEW-18** ✅ `.env.example` is present with all five required variables: `ANTHROPIC_API_KEY`, `OPENAI_API_KEY`, `APP_AI_ENABLED`, `APP_AI_REQUIRE_ADMIN_TOKEN`, `APP_AI_ADMIN_TOKEN`.

---

## Still Open Findings

---

- **ID:** REVIEW-04
- **Severity:** LOW *(downgraded from HIGH — the critical issue of bypassing SLF4J is fixed)*
- **Area:** Backend — Debug Artifact
- **File(s):** `backend/src/main/java/com/exam/aiexamtrainer/service/impl/AiQuestionGenerationServiceImpl.java:152`
- **Status:** PARTIALLY RESOLVED
- **What changed:** Both `System.out.println` calls were replaced with SLF4J calls. The class now properly uses the `@Slf4j` logger.
- **Remaining problem:** The replacement uses `log.info` instead of `log.debug`. The full raw LLM response (potentially hundreds of characters) is logged on every `translatePracticalTask` call at INFO level in production. This is still noisy. Change both lines to `log.debug(...)` so the output is suppressed in production by default.

---

- **ID:** REVIEW-07
- **Severity:** HIGH *(unchanged)*
- **Area:** CI/CD — Deploy Script
- **File(s):** `.github/workflows/deploy-dev.yml`
- **Status:** STILL OPEN — fix attempt was broken and introduced additional bugs
- **Current code:**
  ```bash
  printf "ANTHROPIC_API_KEY=%s\nAPP_AI_ENABLED=%s\nAPP_AI_REQUIRE_ADMIN_TOKEN=%s\nAPP_AI_ADMIN_TOKEN=%s\n" \
    "${{ secrets.ANTHROPIC_API_KEY }}" \
    "${{ secrets.OPENAI_API_KEY }}" \
    "${{ vars.APP_AI_ENABLED }}" \
    "${{ vars.APP_AI_REQUIRE_ADMIN_TOKEN }}" \
    "${{ secrets.APP_AI_ADMIN_TOKEN }}" > .env
  ```
- **Problem:** The format string has **4** `%s` placeholders but **5** arguments are passed. Bash `printf` recycles the format string when there are more arguments than specifiers. The resulting `.env` is:
  ```
  ANTHROPIC_API_KEY=<anthropic_key>      ✓
  APP_AI_ENABLED=<OPENAI_API_KEY value>  ✗  (OpenAI key written to wrong var)
  APP_AI_REQUIRE_ADMIN_TOKEN=<APP_AI_ENABLED value>  ✗
  APP_AI_ADMIN_TOKEN=<APP_AI_REQUIRE_ADMIN_TOKEN value>  ✗
  ANTHROPIC_API_KEY=<APP_AI_ADMIN_TOKEN value>  ✗ (format recycled, overwrites)
  ```
  `OPENAI_API_KEY` is never written. `APP_AI_ENABLED` receives the OpenAI key. All other variables are shifted by one position. The admin token is lost.
- **Fix:** Add `OPENAI_API_KEY=%s\n` to the format string:
  ```bash
  printf "ANTHROPIC_API_KEY=%s\nOPENAI_API_KEY=%s\nAPP_AI_ENABLED=%s\nAPP_AI_REQUIRE_ADMIN_TOKEN=%s\nAPP_AI_ADMIN_TOKEN=%s\n" \
    "${{ secrets.ANTHROPIC_API_KEY }}" \
    "${{ secrets.OPENAI_API_KEY }}" \
    "${{ vars.APP_AI_ENABLED }}" \
    "${{ vars.APP_AI_REQUIRE_ADMIN_TOKEN }}" \
    "${{ secrets.APP_AI_ADMIN_TOKEN }}" > .env
  ```

---

- **ID:** REVIEW-08
- **Severity:** MEDIUM *(unchanged)*
- **Area:** AI/LLM Integration — Prompt Injection
- **File(s):** `backend/src/main/java/com/exam/aiexamtrainer/service/impl/AiPromptBuilder.java`
- **Status:** STILL OPEN — no changes made
- **Problem:** User-supplied `domain`, `difficulty`, `mode`, and `baseQuestion` values are appended directly into LLM prompts without sanitization or allowlist validation. A caller can inject prompt content via newlines in these fields.
- **Recommendation:** Validate `domain` and `difficulty` against the allowed enum values before building the prompt (since `DifficultyLevel` and `ExamMode` enums already exist). For `baseQuestion`, truncate to a maximum length.

---

- **ID:** REVIEW-11
- **Severity:** HIGH *(upgraded from MEDIUM — the fix introduced an active bug)*
- **Area:** Backend — Error Handling
- **File(s):** `backend/src/main/java/com/exam/aiexamtrainer/exception/GlobalExceptionHandler.java`
- **Status:** PARTIALLY RESOLVED — handlers added but `@ResponseStatus` bug causes all ResponseStatusExceptions to return HTTP 500
- **What changed:** `@ExceptionHandler(ResponseStatusException.class)` and `@ExceptionHandler(Exception.class)` handlers were added. The structure and `buildErrorResponse` helper are well-designed.
- **Remaining problem:** The `handleResponseStatus` method has a bare `@ResponseStatus` annotation with no parameters. Spring's `@ResponseStatus` defaults to `HttpStatus.INTERNAL_SERVER_ERROR` when no value is given. This means every `ResponseStatusException` — whether it's a 403 Forbidden (from `ensureAiAccess`) or a 429 Too Many Requests (from `SessionQuotaService`) — will be sent to the client with **HTTP status 500**, while the body correctly shows 403 or 429. The frontend's status-based error handling is broken for all auth and quota errors.
- **Fix:** Remove `@ResponseStatus` from the handler and return `ResponseEntity<Map<String, Object>>` instead:
  ```java
  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<Map<String, Object>> handleResponseStatus(ResponseStatusException ex) {
      HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
      return ResponseEntity.status(status).body(buildErrorResponse(status, ex.getReason()));
  }
  ```

---

- **ID:** REVIEW-12
- **Severity:** MEDIUM *(unchanged)*
- **Area:** Backend — Scalability
- **File(s):** `backend/src/main/java/com/exam/aiexamtrainer/service/impl/AiQuestionGenerationServiceImpl.java:190`
- **Status:** STILL OPEN — no changes made
- **Problem:** `getAllPracticalTasks()` still calls `practicalTaskRepository.findAll()` — an unbounded `SELECT *` over columns holding up to 5000 characters each.

---

- **ID:** REVIEW-13
- **Severity:** MEDIUM *(unchanged)*
- **Area:** Backend — Security
- **File(s):** `backend/src/main/java/com/exam/aiexamtrainer/service/impl/SessionQuotaService.java`
- **Status:** STILL OPEN — no changes made
- **Problem:** AI call quotas are enforced per HTTP session only. Any caller can bypass limits by starting a new session (incognito window, dropping the session cookie, direct API call without cookie).

---

- **ID:** REVIEW-14
- **Severity:** MEDIUM *(unchanged)*
- **Area:** Backend — Input Validation
- **File(s):** `backend/src/main/java/com/exam/aiexamtrainer/controller/AiController.java`
- **Status:** STILL OPEN — no changes made
- **Problem:** All `@RequestBody` parameters in `AiController` lack `@Valid`. JSR-303 constraints on request DTOs would silently be ignored.

---

- **ID:** REVIEW-17
- **Severity:** LOW *(unchanged)*
- **Area:** Docker
- **File(s):** `docker-compose.yml`
- **Status:** STILL OPEN — no changes made
- **Problem:** `depends_on: db` has no `condition: service_healthy`. Backend may attempt Flyway migrations before PostgreSQL is ready, causing crash-restart cycles on cold start.

---

- **ID:** REVIEW-19
- **Severity:** LOW *(unchanged)*
- **Area:** Tests
- **File(s):** `backend/src/test/java/services/SessionQuotaServiceTest.java`
- **Status:** STILL OPEN — note: tests were added but the package is wrong
- **Problem:** `SessionQuotaServiceTest` is in the `services` bare package, not in `com.exam.aiexamtrainer.service` where the class under test lives. Tests were added in this commit (`5f4d9ce`) with the wrong package. The test content is good (4 clear cases, mocked `HttpSession`, verifies both increment and quota-exceeded paths), but the package mismatch is a professionalism gap visible in any IDE.
- **Recommendation:** Move to `com.exam.aiexamtrainer.service` (or `.service.impl`).

---

- **ID:** REVIEW-20
- **Severity:** LOW *(unchanged)*
- **Area:** Backend — Health Check
- **Status:** STILL OPEN — no changes made
- **Problem:** No `spring-boot-starter-actuator`; no machine-readable health endpoint; `docker-compose.yml` backend has no port exposed for direct debugging.

---

- **ID:** REVIEW-21
- **Severity:** LOW *(unchanged)*
- **Area:** Backend — API Documentation
- **Status:** STILL OPEN — no changes made
- **Problem:** No Swagger/OpenAPI documentation. For a portfolio project, `springdoc-openapi-starter-webmvc-ui` is a low-effort addition with high interviewer visibility.

---

## New Findings

---

- **ID:** NEW-01
- **Severity:** HIGH
- **Area:** Backend — Error Handling
- **File(s):** `backend/src/main/java/com/exam/aiexamtrainer/exception/GlobalExceptionHandler.java:33`
- **Problem:** This is the specific bug introduced by the REVIEW-11 partial fix. The `handleResponseStatus` method is annotated with bare `@ResponseStatus` (no parameters). In Spring MVC, `@ResponseStatus` defaults to `HttpStatus.INTERNAL_SERVER_ERROR`. Every `ResponseStatusException` thrown in the application — 403 Forbidden from `ensureAiAccess()`, 429 Too Many Requests from `SessionQuotaService`, 401 Unauthorized from `ensureAiAccess()` — will reach the client as **HTTP 500**. The response body correctly contains `"status": 403` etc., but any HTTP client (including the frontend) that branches on the HTTP status code receives the wrong signal.
- **Evidence:** `AiController.ensureAiAccess()` throws `ResponseStatusException(HttpStatus.FORBIDDEN, ...)`. Before this fix, Spring Boot's default error handling correctly forwarded FORBIDDEN (403). After this fix, `GlobalExceptionHandler.handleResponseStatus()` intercepts it and returns HTTP 500. The frontend's `catch` block checking `error.response?.status === 403` will no longer trigger.
- **Fix:** See REVIEW-11 fix above — return `ResponseEntity` with the exception's own status code instead of relying on `@ResponseStatus`.

---

- **ID:** NEW-02
- **Severity:** LOW
- **Area:** Backend — Logging
- **File(s):** `backend/src/main/java/com/exam/aiexamtrainer/service/impl/AiQuestionGenerationServiceImpl.java:152`
- **Problem:** The two `System.out.println` calls (REVIEW-04) were replaced with `log.info("RAW PRACTICAL TASK TRANSLATION RESPONSE:")` and `log.info(rawResponse)`. This correctly routes through SLF4J, but uses INFO level. On every `translatePracticalTask` call in production, the full raw LLM response is emitted to the log at INFO level — potentially hundreds of characters per call.
- **Recommendation:** Change both to `log.debug(...)`. INFO-level logs should signal meaningful application events, not raw LLM payloads that are only relevant when debugging response parsing failures.

---

## Priority Order for Remaining Work

| Priority | ID | Severity | Issue |
|----------|----|----------|-------|
| 1 | NEW-01 / REVIEW-11 | HIGH | Fix `@ResponseStatus` on `handleResponseStatus` — 403/429 returned as 500 |
| 2 | REVIEW-07 | HIGH | Fix printf format string in deploy script — env vars scrambled on every deploy |
| 3 | REVIEW-08 | MEDIUM | Add allowlist validation for domain/difficulty in `AiPromptBuilder` |
| 4 | REVIEW-14 | MEDIUM | Add `@Valid` to `AiController` request bodies |
| 5 | REVIEW-17 | LOW | Add PostgreSQL healthcheck to `docker-compose.yml` |
| 6 | REVIEW-19 | LOW | Move `SessionQuotaServiceTest` to correct package |
| 7 | REVIEW-04 / NEW-02 | LOW | Downgrade translation response logging from `log.info` to `log.debug` |
| 8 | REVIEW-12 | MEDIUM | Add pagination to `getAllPracticalTasks()` |
| 9 | REVIEW-20 | LOW | Add Spring Boot Actuator |
| 10 | REVIEW-21 | LOW | Add Springdoc OpenAPI |

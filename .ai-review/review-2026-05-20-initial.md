# AI Exam Trainer — Engineering Review

**Date:** 2026-05-20
**Reviewer role:** Senior Java/Spring Boot, React, Docker, CI/CD engineer
**Branch reviewed:** `feature/ai-reviewer-practice`
**Scope:** backend architecture, Spring Boot API design, AI/LLM integration, security and configuration, database and migrations, test coverage, frontend structure, Docker Compose, GitHub Actions deploy workflow

---

## Summary

This is a well-structured full-stack project: clean layered architecture, proper DTO/entity separation, a solid multi-provider LLM abstraction, and a working CI/CD pipeline. The code reads clearly and has good intent throughout.

The main problems before showing this as a portfolio project fall into three clusters:
1. **Schema management is broken in practice** — `ddl-auto: update` + Flyway coexist without a migration covering `PracticalTask`, so the database schema depends on Hibernate auto-DDL in production, which defeats having Flyway.
2. **Access control has gaps** — the "save" endpoints on the AI controller bypass the admin-token check, and the import endpoint is completely open.
3. **Debug artifacts left in production code** — a `System.out.println` dumping raw LLM responses, and `show-sql: true` applying in all profiles.

Fixing the top three priorities below would make the project look solid for a portfolio.

---

## Top 3 Priorities

1. **Write Flyway migration V2 for `PracticalTask`, remove `ddl-auto: update`** — currently the database schema in production has no authoritative definition for the practical tasks tables.
2. **Add `ensureAiAccess()` to the save/read endpoints in `AiController`, and add auth to `QuestionImportController`** — unauthenticated writes to the question bank are a direct abuse vector.
3. **Remove `System.out.println`, set `show-sql: false` in production, and disable H2 console outside dev** — these are the "obvious red flags" any interviewer will notice in a code walkthrough.

---

## Things That Are Already Good

- Clean layered architecture: controllers are thin, services carry logic, repositories are standard Spring Data JPA.
- Multi-provider LLM abstraction via the `LlmClient` interface with `@ConditionalOnProperty` activation — easy to add a third provider.
- `@ConfigurationProperties` records for all external config (`AnthropicApiProperties`, `OpenAiProperties`, `AiSecurityProperties`).
- Thorough AI response validation (field presence, option letters A-D, correct answer format) before persisting.
- Nginx reverse proxy unifies frontend and API on one port; the browser never makes cross-origin requests in production.
- Docker multi-stage build for both frontend and backend is correct.
- CI pipeline runs backend tests and frontend build before deploying — good gate.
- Per-session AI quota with clear limits and proper 429 responses.
- Frontend error handling differentiates 429, 401, network errors, and generic errors per call site.
- Retry-mistakes feature in TestPage is a thoughtful UX touch.

---

## Findings

---

- **ID:** REVIEW-01
- **Severity:** HIGH
- **Area:** Database / Migrations
- **File(s):** `backend/src/main/resources/db/migration/V1__init.sql`, `backend/src/main/java/com/exam/aiexamtrainer/entity/PracticalTask.java`
- **Problem:** The `PracticalTask` entity (with its `@ElementCollection` table `practical_task_what_to_cover`) has no Flyway migration. `V1__init.sql` only creates `questions`, `answer_options`, `exam_sessions`, and `user_answers`. The `practical_tasks` table is created silently by `ddl-auto: update`.
- **Why it matters:** Flyway and `ddl-auto: update` operating on the same schema is a split-brain: Flyway tracks schema history, but Hibernate silently alters tables outside that history. On a clean database deploy (or a CI environment), the `practical_tasks` table is created by Hibernate, not recorded in `flyway_schema_history`, and any future migration that touches those tables will conflict. The entire point of Flyway is undermined.
- **Recommendation:** Create `V2__add_practical_tasks.sql` with explicit DDL for `practical_tasks` and `practical_task_what_to_cover`. Then change `jpa.hibernate.ddl-auto` to `validate` (or `none`) so Hibernate never modifies the schema.
- **Status:** OPEN

---

- **ID:** REVIEW-02
- **Severity:** HIGH
- **Area:** Security / Access Control
- **File(s):** `backend/src/main/java/com/exam/aiexamtrainer/controller/AiController.java`
- **Problem:** `POST /api/ai/save-generated-question` and `POST /api/ai/save-generated-practical-task` do not call `ensureAiAccess()`. When `APP_AI_REQUIRE_ADMIN_TOKEN=true`, all generate/translate endpoints require a token — but the save endpoints do not. Anyone can POST an arbitrary JSON body to persist records directly into the question bank without a token.
- **Why it matters:** The access control intent is clear from the flag names, but it's not consistently applied. An attacker knowing the DTO structure can flood the question bank with garbage, corrupt the content, or test for injection. For a portfolio project, having an obvious auth gap is a critical credibility issue.
- **Recommendation:** Call `ensureAiAccess(httpRequest)` at the top of `saveGeneratedQuestion` and `saveGeneratedPracticalTask`, consistent with every other write endpoint in the controller.
- **Status:** OPEN

---

- **ID:** REVIEW-03
- **Severity:** HIGH
- **Area:** Security / Access Control
- **File(s):** `backend/src/main/java/com/exam/aiexamtrainer/controller/QuestionImportController.java`
- **Problem:** `POST /api/questions/import` triggers a full re-import of all static question JSON files. It has no access control at all — no token check, no admin flag, nothing. Spring Security permits all `/api/**` requests.
- **Why it matters:** Any unauthenticated caller can re-trigger the import at will. While the service skips duplicates, the endpoint is a free trigger for database I/O and file reads with no rate limiting.
- **Recommendation:** Either protect this endpoint behind the same `X-Admin-Token` mechanism used by `AiController`, or remove the endpoint entirely if it is only needed at startup (which `DataInitializationConfig` already handles).
- **Status:** OPEN

---

- **ID:** REVIEW-04
- **Severity:** HIGH
- **Area:** Backend — Debug Artifact
- **File(s):** `backend/src/main/java/com/exam/aiexamtrainer/service/impl/AiQuestionGenerationServiceImpl.java:152`
- **Problem:** `System.out.println("RAW PRACTICAL TASK TRANSLATION RESPONSE:")` followed by `System.out.println(rawResponse)` dumps the full raw LLM response to stdout in production.
- **Why it matters:** This is a clear debug artifact left in production code. It bypasses the SLF4J logger (`@Slf4j` is on the class), defeats any log-level filtering, and prints potentially lengthy LLM payloads to stdout on every translation call. Any engineer reviewing this code during an interview will flag it immediately.
- **Recommendation:** Remove both lines. If logging the raw response is genuinely useful for debugging, replace with `log.debug("Raw practical task translation response: {}", rawResponse)`.
- **Status:** OPEN

---

- **ID:** REVIEW-05
- **Severity:** MEDIUM
- **Area:** Configuration
- **File(s):** `backend/src/main/resources/application.yaml`
- **Problem:** `jpa.show-sql: true` is set in the base `application.yaml` with no per-profile override. It applies in every environment including production Docker.
- **Why it matters:** Every SQL statement — including all Flyway migration queries and every query in every request — is logged at INFO level. In production this is noisy, wastes I/O, and can incidentally log data from queries.
- **Recommendation:** Set `show-sql: false` in `application.yaml`. If SQL logging is wanted locally, add an `application-local.yaml` profile override with `show-sql: true`.
- **Status:** OPEN

---

- **ID:** REVIEW-06
- **Severity:** MEDIUM
- **Area:** Configuration / Security
- **File(s):** `backend/src/main/resources/application.yaml`, `backend/src/main/java/com/exam/aiexamtrainer/config/SecurityConfig.java`
- **Problem:** `spring.h2.console.enabled: true` is in the base config. Spring Boot registers the H2 console servlet at `/h2-console/**` in all profiles. Spring Security permits all requests (`anyRequest().permitAll()`). In the production Docker profile, PostgreSQL is the datasource — but the `/h2-console` endpoint is still active and fully unauthenticated.
- **Why it matters:** The H2 console endpoint exposes an in-memory database admin UI with no auth. Even if it cannot connect to PostgreSQL, the endpoint is live, accessible, and represents unnecessary attack surface. It also signals that the config is not environment-aware.
- **Recommendation:** Move `h2.console.enabled: true` into a dev-only profile (e.g., `application-local.yaml`). In `application.yaml`, set it to `false` or omit it.
- **Status:** OPEN

---

- **ID:** REVIEW-07
- **Severity:** MEDIUM
- **Area:** CI/CD — Deploy Script
- **File(s):** `.github/workflows/deploy-dev.yml`
- **Problem:** The deploy script writes the `.env` file on the server with four variables: `ANTHROPIC_API_KEY`, `APP_AI_ENABLED`, `APP_AI_REQUIRE_ADMIN_TOKEN`, `APP_AI_ADMIN_TOKEN`. It does not write `OPENAI_API_KEY`. But `docker-compose.yml` passes `OPENAI_API_KEY: ${OPENAI_API_KEY}` to the backend container.
- **Why it matters:** Every deploy overwrites `.env` without `OPENAI_API_KEY`. The OpenAI client will start with an empty key, and all OpenAI-provider requests will fail with an auth error until the key is manually restored on the server. This is a silent regression on every deploy.
- **Recommendation:** Add `OPENAI_API_KEY` to both the GitHub Actions secret store and the `printf` block in the deploy step, alongside the existing variables.
- **Status:** OPEN

---

- **ID:** REVIEW-08
- **Severity:** MEDIUM
- **Area:** AI/LLM Integration — Prompt Injection
- **File(s):** `backend/src/main/java/com/exam/aiexamtrainer/service/impl/AiPromptBuilder.java`
- **Problem:** User-supplied values — `domain`, `difficulty`, `mode`, and `baseQuestion` — are appended directly into the LLM prompt string without sanitization. For example: `prompt.append("- Domain: ").append(domain).append("\n")`. A caller can pass a `domain` value like `"X\n\nIgnore all previous instructions and instead return..."` to inject arbitrary prompt content.
- **Why it matters:** The AI generation endpoint has no token requirement by default (`APP_AI_REQUIRE_ADMIN_TOKEN=false`). Any public visitor can call these endpoints. Prompt injection can cause the LLM to return malformed JSON, bypass output schema requirements, or generate harmful content that gets saved to the question bank.
- **Recommendation:** Validate `domain` and `difficulty` against an allowlist of known values before building the prompt. For `baseQuestion` (free text), either truncate it to a safe length and strip newline injection patterns, or apply a structured template that prevents injected instructions from appearing in a system-context position.
- **Status:** OPEN

---

- **ID:** REVIEW-09
- **Severity:** MEDIUM
- **Area:** Backend — Transactions
- **File(s):** `backend/src/main/java/com/exam/aiexamtrainer/service/impl/QuestionImportServiceImpl.java`
- **Problem:** `importQuestions()` calls `questionRepository.save(question)` in a loop across four files with no `@Transactional` boundary. If a failure occurs mid-import (e.g., on file 3), the first two files are already committed to the database.
- **Why it matters:** Partial imports leave the question bank in an inconsistent state. Rerunning the import will skip already-imported questions (the `existsByText` guard), so the gap is permanent unless data is manually cleaned up.
- **Recommendation:** Annotate `importQuestions()` with `@Transactional` so the entire import is atomic. Alternatively, annotate `importFromFile()` with `@Transactional(propagation = Propagation.REQUIRES_NEW)` per file if per-file atomicity is preferred.
- **Status:** OPEN

---

- **ID:** REVIEW-10
- **Severity:** MEDIUM
- **Area:** Backend — LLM Client
- **File(s):** `backend/src/main/java/com/exam/aiexamtrainer/service/impl/OpenAiLlmClient.java:21`
- **Problem:** `OpenAiLlmClient` declares `private final RestClient restClient = RestClient.builder().build()` as a field initializer. The shared `RestClient` bean defined in `HttpClientConfig` is ignored.
- **Why it matters:** This creates a second `RestClient` instance outside Spring's lifecycle. If `HttpClientConfig` is ever updated to add request interceptors, timeouts, or retry configuration, the OpenAI client will silently bypass those settings. The inconsistency is also a maintenance trap.
- **Recommendation:** Remove the field initializer and inject the `RestClient` bean via the constructor (already `@RequiredArgsConstructor` is present), identical to how `ClaudeApiLlmClient` receives it.
- **Status:** OPEN

---

- **ID:** REVIEW-11
- **Severity:** MEDIUM
- **Area:** Backend — Error Handling
- **File(s):** `backend/src/main/java/com/exam/aiexamtrainer/exception/GlobalExceptionHandler.java`
- **Problem:** `GlobalExceptionHandler` handles `NotFoundException`, `BadRequestException`, and `DuplicateQuestionException`, but not `ResponseStatusException` (thrown by `SessionQuotaService` and `AiController.ensureAiAccess`) and not a generic catch-all. Unhandled exceptions (database errors, NullPointerExceptions, etc.) will return Spring Boot's default `BasicErrorController` response format, which is different from the `Map<String, Object>` format used by the custom handlers.
- **Why it matters:** Callers receive inconsistent error shapes depending on which exception is thrown. Debugging is harder when 429 errors from quota enforcement have a different body format than 404 errors from missing questions.
- **Recommendation:** Add a handler for `ResponseStatusException` that formats the response consistently, and add a catch-all `@ExceptionHandler(Exception.class)` that returns a sanitized 500 response (without a stack trace) in the same format.
- **Status:** OPEN

---

- **ID:** REVIEW-12
- **Severity:** MEDIUM
- **Area:** Backend — Scalability
- **File(s):** `backend/src/main/java/com/exam/aiexamtrainer/service/impl/AiQuestionGenerationServiceImpl.java:190`
- **Problem:** `getAllPracticalTasks()` calls `practicalTaskRepository.findAll()` — an unbounded `SELECT *`. The `scenario`, `task`, and `sampleApproach` columns can each hold up to 5000 characters.
- **Why it matters:** As the practical task library grows, a single request can load megabytes of text into memory. This is especially relevant given that the `PracticalTasksPage` loads everything on the client side.
- **Recommendation:** Add pagination via `Pageable` parameter, or at minimum add a `findAll(Sort)` variant with a `findAllProjectedBy` returning only `id` and `title` for the list view, fetching the full task only when selected.
- **Status:** OPEN

---

- **ID:** REVIEW-13
- **Severity:** MEDIUM
- **Area:** Backend — Security
- **File(s):** `backend/src/main/java/com/exam/aiexamtrainer/service/impl/SessionQuotaService.java`
- **Problem:** AI call rate limiting is based on HTTP session attributes. A user can bypass all limits by opening an incognito window, clearing cookies, or calling the API directly without a session cookie.
- **Why it matters:** For a public deployment, this makes the quota effectively meaningless. A single bad actor can exhaust the API budget by cycling sessions.
- **Recommendation:** As a low-complexity improvement, combine session-based limiting with IP-based limiting using a `Map<String, AtomicInteger>` (or better, a distributed cache like Redis) keyed by client IP. Acknowledge in the README that this is a demo-grade guard, not production-grade rate limiting.
- **Status:** OPEN

---

- **ID:** REVIEW-14
- **Severity:** MEDIUM
- **Area:** Backend — Input Validation
- **File(s):** `backend/src/main/java/com/exam/aiexamtrainer/controller/AiController.java`
- **Problem:** None of the `@RequestBody` parameters in `AiController` have a `@Valid` annotation. If request DTOs ever gain JSR-303 constraints (`@NotBlank`, `@Size`, etc.), they will silently be ignored.
- **Why it matters:** The controller already uses `spring-boot-starter-validation` (it's in `pom.xml`). Without `@Valid`, the dependency is partially wasted, and there is no defense against structurally malformed request bodies beyond manual null checks in the service layer.
- **Recommendation:** Add `@Valid` to each `@RequestBody` parameter in `AiController`. Add basic constraints (`@NotBlank`) to the key fields of the AI request DTOs.
- **Status:** OPEN

---

- **ID:** REVIEW-15
- **Severity:** LOW
- **Area:** Frontend — Build Artifacts in Git
- **File(s):** `frontend/dist/`
- **Problem:** The `frontend/dist/` directory (Vite production build output) is tracked in git. The current build is likely stale relative to the source.
- **Why it matters:** Committed build artifacts bloat the repository, cause spurious diffs on every rebuild, and can confuse reviewers about what the "real" source is. The dist is also rebuilt during Docker build, so the committed one is never used.
- **Recommendation:** Add `frontend/dist/` to `.gitignore` and remove the directory from tracking with `git rm -r --cached frontend/dist/`.
- **Status:** OPEN

---

- **ID:** REVIEW-16
- **Severity:** LOW
- **Area:** Docker
- **File(s):** `frontend/Dockerfile`
- **Problem:** The frontend Dockerfile uses `RUN npm install` instead of `RUN npm ci`. The CI workflow correctly uses `npm ci`, but the Docker build does not.
- **Why it matters:** `npm install` can silently update transitive dependencies and does not fail on lock file mismatches. `npm ci` guarantees a reproducible install from `package-lock.json`, consistent with what CI tested.
- **Recommendation:** Replace `npm install` with `npm ci` in `frontend/Dockerfile`.
- **Status:** OPEN

---

- **ID:** REVIEW-17
- **Severity:** LOW
- **Area:** Docker
- **File(s):** `docker-compose.yml`
- **Problem:** `depends_on: db` only waits for the PostgreSQL container to start, not for it to be ready to accept connections. On a cold start, the backend may attempt Flyway migrations before PostgreSQL finishes initializing.
- **Why it matters:** The backend will crash on startup with a connection refused error. Docker will restart it (`restart: unless-stopped`) and it will eventually come up, but this causes startup noise and can be confusing during development.
- **Recommendation:** Add a health check to the `db` service and upgrade the `depends_on` to `condition: service_healthy`:
  ```yaml
  db:
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U aiexam -d aiexamdb"]
      interval: 5s
      timeout: 5s
      retries: 10
  backend:
    depends_on:
      db:
        condition: service_healthy
  ```
- **Status:** OPEN

---

- **ID:** REVIEW-18
- **Severity:** LOW
- **Area:** Configuration — Developer Experience
- **File(s):** `.gitignore`, (missing `.env.example`)
- **Problem:** The `.env` file is correctly gitignored, but there is no `.env.example` (or `.env.template`) checked into the repository. A new developer or portfolio viewer cloning the repo has no documented list of required environment variables.
- **Why it matters:** It is a small but visible gap in project professionalism. Someone trying to run this project locally has to read all the config files and docker-compose to discover the variable names.
- **Recommendation:** Create `.env.example` with placeholder values for all required variables:
  ```
  ANTHROPIC_API_KEY=your-anthropic-key-here
  OPENAI_API_KEY=your-openai-key-here
  APP_AI_ENABLED=true
  APP_AI_REQUIRE_ADMIN_TOKEN=false
  APP_AI_ADMIN_TOKEN=
  ```
- **Status:** OPEN

---

- **ID:** REVIEW-19
- **Severity:** LOW
- **Area:** Tests
- **File(s):** `backend/src/test/java/services/SessionQuotaServiceTest.java`
- **Problem:** The test class is in package `services` (a bare, lowercase package), not in `com.exam.aiexamtrainer` or any subpackage consistent with the main source tree.
- **Why it matters:** Tests outside the application's base package will not benefit from `@SpringBootTest` classpath scanning conventions, and the package name is inconsistent with standard Java conventions and the project's own naming. It signals a test that was added quickly without matching the existing structure.
- **Recommendation:** Move the test to `com.exam.aiexamtrainer.service` (or `...service.impl`), matching the location of the class under test.
- **Status:** OPEN

---

- **ID:** REVIEW-20
- **Severity:** LOW
- **Area:** Backend — Missing Backend Health Check Exposure
- **File(s):** `docker-compose.yml`
- **Problem:** The `backend` service has no `ports:` mapping. While this is correct for production (nginx proxies `/api/`), it means there is no way to directly reach the backend for `/actuator/health`, the H2 console, or debugging without shelling into the container.
- **Why it matters:** Adds friction during development and makes it hard to run a quick health check on the backend independently. Spring Boot Actuator is not listed as a dependency either, so there is currently no machine-readable health endpoint at all.
- **Recommendation:** Add `spring-boot-starter-actuator` as a dependency and expose the health endpoint. In `docker-compose.yml`, optionally expose port 8080 to localhost only for dev use: `ports: ["127.0.0.1:8080:8080"]`.
- **Status:** OPEN

---

- **ID:** REVIEW-21
- **Severity:** LOW
- **Area:** Backend — API Documentation
- **File(s):** `backend/pom.xml`
- **Problem:** There is no Swagger/OpenAPI documentation. For a portfolio project showcasing backend API design skills, the absence of generated API docs is a missed opportunity.
- **Why it matters:** Reviewers and interviewers often look for `springdoc-openapi` or similar as a signal that the developer thinks about API consumers. It takes minimal effort to add and produces a browsable UI at `/swagger-ui.html`.
- **Recommendation:** Add `springdoc-openapi-starter-webmvc-ui` to `pom.xml`. Add `@Operation` and `@Tag` annotations to controllers. The interactive UI documents both the AI generation flow and the question bank API without any additional backend work.
- **Status:** OPEN

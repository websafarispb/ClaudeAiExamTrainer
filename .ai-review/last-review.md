# AI Exam Trainer — Final Release Review

**Date:** 2026-05-27
**Previous reviews:** 2026-05-20 (initial), 2026-05-27 (follow-up)
**Branch:** `feature/ai-reviewer-practice`
**Scope:** Final pre-push check — HIGH/MEDIUM issues only, regressions, portfolio blockers

---

## Verdict

**✅ Ready to push as a portfolio project.**

All HIGH-severity issues from two previous review rounds are resolved. No regressions were found. The remaining open items are all MEDIUM or LOW, and none of them would cause a reasonable interviewer to reject the project — they are either known tradeoffs for a demo application or cosmetic gaps.

---

## What Was Fixed in This Round (2 commits)

### `2c10bed` — Fix follow-up review critical issues

| Finding | What was done |
|---------|--------------|
| **REVIEW-07** (HIGH) — deploy printf format mismatch | Added `OPENAI_API_KEY=%s\n` to the format string. 5 placeholders now match 5 arguments. `.env` is written correctly on every deploy. |
| **NEW-01 / REVIEW-11** (HIGH) — `@ResponseStatus` defaulting to 500 | Removed bare `@ResponseStatus`. `handleResponseStatus` now returns `ResponseEntity<Map<String, Object>>` with `ResponseEntity.status(status)`. 403/429/401 are now correctly forwarded to clients. |

### `ca08d49` — Add database healthcheck to Docker Compose

| Finding | What was done |
|---------|--------------|
| **REVIEW-17** (LOW) — no Postgres readiness check | `db` service now has `healthcheck` (`pg_isready`). `backend` now depends on `db` with `condition: service_healthy`. Cold-start crash loops are eliminated. |
| **REVIEW-04 / NEW-02** (LOW) — raw LLM response logged at INFO | Changed `log.info(rawResponse)` → `log.debug(rawResponse)`. Production logs are no longer polluted with full LLM payloads. |

---

## Remaining Open Issues

All remaining issues are MEDIUM or LOW. None block portfolio use.

---

### REVIEW-08 — Prompt Injection in AiPromptBuilder
- **Severity:** MEDIUM
- **File:** `backend/src/main/java/com/exam/aiexamtrainer/service/impl/AiPromptBuilder.java`
- **Status:** OPEN — no changes made across all review rounds
- **Problem:** `domain`, `difficulty`, `mode`, and `baseQuestion` are appended directly into LLM prompts. A caller can inject newlines and arbitrary instructions.
- **Portfolio impact:** Low. The generate endpoints are behind `ensureAiAccess()` (which requires a token when `requireAdminToken=true`), and the practical task prompt uses `String.formatted()` with a fixed structure that limits injection surface. An interviewer who specifically asks about prompt injection hardening will find this gap. The recommended fix — validate `domain` and `difficulty` against the existing `DifficultyLevel`/`ExamMode` enums before building the prompt — is a 5-line change.

---

### REVIEW-12 — Unbounded `findAll()` on Practical Tasks
- **Severity:** MEDIUM
- **File:** `backend/src/main/java/com/exam/aiexamtrainer/service/impl/AiQuestionGenerationServiceImpl.java:192`
- **Status:** OPEN — no changes made
- **Problem:** `getAllPracticalTasks()` calls `practicalTaskRepository.findAll()` with no pagination. Each record can hold up to 5000 characters across several columns.
- **Portfolio impact:** Negligible at demo scale. The practical task library will have a small number of entries. This would only become a real problem at hundreds of records. Acceptable as a known limitation.

---

### REVIEW-13 — Session-Only Rate Limiting
- **Severity:** MEDIUM
- **File:** `backend/src/main/java/com/exam/aiexamtrainer/service/impl/SessionQuotaService.java`
- **Status:** OPEN — no changes made
- **Problem:** AI quotas are per HTTP session. A caller can bypass limits by dropping the session cookie or calling the API directly.
- **Portfolio impact:** Acceptable for a demo. Any interviewer who probes this will find it, but a simple honest answer ("this is demo-grade session limiting; production would use IP-based or user-based limiting with Redis") is a stronger signal than not having rate limiting at all. The existing implementation demonstrates the intent clearly.

---

### REVIEW-14 — `@Valid` Missing on AiController Request Bodies
- **Severity:** MEDIUM
- **File:** `backend/src/main/java/com/exam/aiexamtrainer/controller/AiController.java`
- **Status:** OPEN — no changes made
- **Problem:** `@RequestBody` parameters lack `@Valid`. No constraints are defined on the AI request DTOs either, so this has no current runtime effect.
- **Portfolio impact:** Cosmetic. No validation constraints exist on the DTOs, so adding `@Valid` without also adding `@NotBlank` etc. would accomplish nothing. This is a no-op gap rather than a security hole. Fix both together or leave both out.

---

### REVIEW-19 — SessionQuotaServiceTest in Wrong Package (LOW)
- **Severity:** LOW
- **File:** `backend/src/test/java/services/SessionQuotaServiceTest.java`
- **Problem:** Package is `services`, not `com.exam.aiexamtrainer.service`. Visible in any IDE.
- **Fix:** Move to `com.exam.aiexamtrainer.service` — a rename in IntelliJ takes 10 seconds.

---

### Hardcoded Database Credentials (LOW — not previously flagged)
- **Severity:** LOW
- **File:** `docker-compose.yml`
- **Problem:** `POSTGRES_PASSWORD: aiexam` is hardcoded in `docker-compose.yml` alongside the username and database name. These same values appear in the backend environment block. For a demo project this is standard practice, but an interviewer looking at the compose file will note it.
- **Portfolio impact:** Minimal. This is ubiquitous in demo and tutorial projects. Optionally move the DB password to `.env` with `${POSTGRES_PASSWORD:-aiexam}` to show awareness of the pattern, but it is not required.

---

## High-Severity History — All Resolved

For completeness, every HIGH finding across all review rounds is now closed:

| ID | Issue | Resolution |
|----|-------|-----------|
| REVIEW-01 | No Flyway migration for `PracticalTask`; `ddl-auto: update` | `V2__add_practical_tasks.sql` added; `ddl-auto: validate` |
| REVIEW-02 | Save endpoints bypassed `ensureAiAccess()` | `ensureAiAccess()` added to both save methods |
| REVIEW-03 | Import endpoint had no auth | `ensureAdminAccess()` added |
| REVIEW-04 | `System.out.println` dumping raw LLM responses | Replaced with `log.debug()` |
| REVIEW-05 | `show-sql: true` in all profiles | Set to `false` in base config |
| REVIEW-06 | H2 console enabled in all profiles | Set to `false` in base config |
| REVIEW-07 | `OPENAI_API_KEY` missing / printf format mismatch | Format string corrected; 5 vars written correctly |
| REVIEW-10 | `OpenAiLlmClient` ignoring shared `RestClient` bean | Now injected via constructor |
| REVIEW-11 | No handler for `ResponseStatusException`; bare `@ResponseStatus` returning 500 | Returns `ResponseEntity` with correct status |
| NEW-01 | Regression: bare `@ResponseStatus` on handler | Fixed in same commit as REVIEW-11 |
| REVIEW-15 | `frontend/dist/` committed to git | Gitignored and untracked |
| REVIEW-16 | `npm install` in frontend Dockerfile | Changed to `npm ci` |
| REVIEW-17 | No Postgres healthcheck; backend races DB on cold start | Healthcheck + `condition: service_healthy` added |
| REVIEW-18 | No `.env.example` | `.env.example` with all 5 vars created |

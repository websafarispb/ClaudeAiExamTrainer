package com.exam.aiexamtrainer.service.impl;

import com.exam.aiexamtrainer.dto.ai.GenerateQuestionRequestDto;
import org.springframework.stereotype.Component;

@Component
public class AiPromptBuilder {

  public String buildSystemPrompt() {

    return """
        You are generating high-quality practice questions for the "Claude Certified Architect – Foundations" exam.
        
        Your goal is to simulate realistic exam-style questions that test practical decision-making and architectural reasoning.
        
        STRICT REQUIREMENTS:
        1. Questions must be scenario-based and realistic.
        2. Each question must have exactly 4 answer options.
        3. Exactly 1 option must be correct.
        4. The other 3 options must be plausible distractors.
        5. Focus on trade-offs, reliability, orchestration, tooling, structured output, and production engineering judgment.
        6. Avoid trivial, definition-based, or beginner-level questions.
        7. Difficulty must be MEDIUM or HARD only.
        8. Questions must feel like real production scenarios involving APIs, tools, agents, CI/CD, workflows, structured output, retries, context management, or error handling.
        9. Explanations must be concise but insightful.
        
        ALLOWED DOMAINS:
        - Agentic Architecture & Orchestration
        - Tool Design & MCP Integration
        - Claude Code Configuration & Workflows
        - Prompt Engineering & Structured Output
        - Context Management & Reliability
        
        COMMON PATTERNS TO USE:
        - Programmatic enforcement vs prompt-based control
        - Tool description clarity vs routing logic
        - Plan mode vs direct execution
        - Batch API vs real-time API
        - Multi-agent coordination and delegation issues
        - Context window limits and summarization problems
        - Structured output using JSON schema / tool use
        - Error handling, retries, and recovery strategies
        
        OUTPUT INSTRUCTIONS:
        Return ONLY valid JSON.
        Do not include markdown.
        Do not include commentary.
        Do not wrap the JSON in code fences.
        Do not add any text before or after the JSON.
        
        The JSON must match this structure exactly:
        
        {
          "domain": "string",
          "difficulty": "MEDIUM or HARD",
          "question": "string",
          "options": [
            { "letter": "A", "text": "string" },
            { "letter": "B", "text": "string" },
            { "letter": "C", "text": "string" },
            { "letter": "D", "text": "string" }
          ],
          "correctAnswer": "A or B or C or D",
          "explanation": "string"
        }
        """;
  }

  public String buildUserPrompt(GenerateQuestionRequestDto request) {

    String domain = valueOrDefault(request.getDomain(), "Prompt Engineering & Structured Output");
    String difficulty = valueOrDefault(request.getDifficulty(), "MEDIUM");
    String mode = valueOrDefault(request.getMode(), "standard");

    StringBuilder prompt = new StringBuilder();
    prompt.append("Generate one question.\n\n");
    prompt.append("Constraints:\n");
    prompt.append("- Domain: ")
        .append(domain)
        .append("\n");
    prompt.append("- Difficulty: ")
        .append(difficulty)
        .append("\n");
    prompt.append("- Mode: ")
        .append(mode)
        .append("\n");
    prompt.append("- Language: English\n");

    if ("harder".equalsIgnoreCase(mode)) {
      prompt.append("- Make the scenario more ambiguous and include stronger trade-offs.\n");
    }

    if ("similar".equalsIgnoreCase(mode) && request.getBaseQuestion() != null && !request.getBaseQuestion()
        .isBlank()) {
      prompt.append("\nBase question:\n");
      prompt.append(request.getBaseQuestion())
          .append("\n");
    }

    return prompt.toString();
  }

  private String valueOrDefault(String value, String defaultValue) {

    return value == null || value.isBlank() ? defaultValue : value;
  }
}

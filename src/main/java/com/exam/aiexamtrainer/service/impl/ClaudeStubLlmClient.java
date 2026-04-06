package com.exam.aiexamtrainer.service.impl;

import com.exam.aiexamtrainer.enums.LlmProvider;
import com.exam.aiexamtrainer.service.LlmClient;
import org.springframework.stereotype.Component;

//@Component
public class ClaudeStubLlmClient implements LlmClient {

  @Override
  public LlmProvider getProvider() {

    return LlmProvider.CLAUDE;
  }

  @Override
  public String generate(String systemPrompt, String userPrompt) {

    if (systemPrompt.contains("practical tasks")) {
      return """
          {
            "domain": "Tool Design & MCP Integration",
            "difficulty": "HARD",
            "title": "Design a Reliable Tool Invocation Workflow",
            "scenario": "Your team is building an internal assistant that can call multiple tools to retrieve account data, incident history, and policy details. Users complain that the assistant sometimes chooses the wrong tool or skips required validation before making a call.",
            "task": "Describe how you would redesign the workflow to improve tool selection reliability and reduce unsafe or incorrect tool invocations.",
            "whatToCover": [
              "How tool descriptions should be improved",
              "When to use programmatic enforcement instead of prompt-only instructions",
              "How to validate tool inputs and outputs"
            ],
            "sampleApproach": "A strong answer would propose clearer tool contracts, tighter tool descriptions, structured validation before execution, and explicit programmatic safeguards for high-risk operations."
          }
          """;
    }

    return """
        {
          "domain": "Prompt Engineering & Structured Output",
          "difficulty": "HARD",
          "question": "A platform team is building an internal service that uses Claude to extract structured procurement data from PDFs. The downstream workflow rejects any response that contains extra prose outside the schema. During testing, the team notices that the model occasionally adds short explanatory notes before the JSON payload, causing the pipeline to fail. What is the best approach to improve reliability?",
          "options": [
            { "letter": "A", "text": "Add stronger wording in the prompt telling Claude not to include extra text" },
            { "letter": "B", "text": "Use structured output with an explicit JSON schema and validate the response before passing it downstream" },
            { "letter": "C", "text": "Increase the context window so Claude has more room to format the response correctly" },
            { "letter": "D", "text": "Split the workflow into two separate prompts, one for reasoning and one for formatting, without validation" }
          ],
          "correctAnswer": "B",
          "explanation": "B is correct because schema-constrained structured output plus validation is the most reliable approach for machine-readable downstream workflows."
        }
        """;
  }
}
package com.exam.aiexamtrainer.service.impl;

import com.exam.aiexamtrainer.service.LlmClient;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class StubLlmClient implements LlmClient {

  @Override
  public String generate(String systemPrompt, String userPrompt) {

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
          "explanation": "B is correct because schema-constrained structured output plus validation is the most reliable approach for machine-readable downstream workflows. A is weaker because prompt-only control is not reliable enough. C does not solve format compliance directly. D may help organize prompting, but without validation it still allows malformed output to reach the pipeline."
        }
        """;
  }
}

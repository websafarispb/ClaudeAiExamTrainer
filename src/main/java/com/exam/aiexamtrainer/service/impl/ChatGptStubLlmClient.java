package com.exam.aiexamtrainer.service.impl;

import com.exam.aiexamtrainer.enums.LlmProvider;
import com.exam.aiexamtrainer.service.LlmClient;
import org.springframework.stereotype.Component;

@Component
public class ChatGptStubLlmClient implements LlmClient {

  @Override
  public LlmProvider getProvider() {

    return LlmProvider.CHATGPT;
  }

  @Override
  public String generate(String systemPrompt, String userPrompt) {

    if (systemPrompt.contains("practical tasks")) {
      return """
          {
            "domain": "Context Management & Reliability",
            "difficulty": "HARD",
            "title": "Prevent Context Loss in Multi-Step Agent Workflows",
            "scenario": "An engineering team uses one model to summarize long incident threads and a second model to generate remediation plans. Important constraints are sometimes lost between steps, leading to incorrect recommendations.",
            "task": "Explain how you would redesign the workflow to preserve critical context across steps and improve downstream decision quality.",
            "whatToCover": [
              "What information must always be preserved",
              "Why free-form summaries are risky",
              "How structured intermediate outputs can improve reliability"
            ],
            "sampleApproach": "A strong answer would recommend structured summaries, mandatory fields for constraints and decisions, and validation to ensure that required context is not lost between pipeline stages."
          }
          """;
    }

    return """
        {
          "domain": "Context Management & Reliability",
          "difficulty": "HARD",
          "question": "An engineering team uses an LLM to summarize long incident threads before passing the summaries to another agent that drafts remediation plans. Over time, the team notices that important constraints are occasionally dropped from the summaries, causing poor downstream decisions. Which approach is most appropriate to improve reliability?",
          "options": [
            { "letter": "A", "text": "Increase the temperature so the summaries become more creative and detailed" },
            { "letter": "B", "text": "Replace the summarization step with a longer prompt and hope the model keeps all critical details" },
            { "letter": "C", "text": "Define a structured summarization format that explicitly preserves constraints, decisions, and open issues" },
            { "letter": "D", "text": "Use fewer incident messages so the model has less context to process" }
          ],
          "correctAnswer": "C",
          "explanation": "C is correct because structured summaries reduce the chance that critical constraints are lost between stages in a multi-step workflow."
        }
        """;
  }
}
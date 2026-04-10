package com.exam.aiexamtrainer.service;

import com.exam.aiexamtrainer.enums.LlmProvider;

public interface LlmClient {

  LlmProvider getProvider();

  String generate(String systemPrompt, String userPrompt);
}
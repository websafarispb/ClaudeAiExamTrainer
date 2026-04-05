package com.exam.aiexamtrainer.service;

public interface LlmClient {

  String generate(String systemPrompt, String userPrompt);
}
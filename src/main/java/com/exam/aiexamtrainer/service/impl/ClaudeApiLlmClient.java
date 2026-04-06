package com.exam.aiexamtrainer.service.impl;

import com.exam.aiexamtrainer.config.AnthropicApiProperties;
import com.exam.aiexamtrainer.enums.LlmProvider;
import com.exam.aiexamtrainer.exception.BadRequestException;
import com.exam.aiexamtrainer.service.LlmClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
@ConditionalOnProperty(prefix = "anthropic.api", name = "key")
public class ClaudeApiLlmClient implements LlmClient {

  private final RestClient restClient;
  private final AnthropicApiProperties properties;

  public ClaudeApiLlmClient(RestClient restClient, AnthropicApiProperties properties) {

    this.restClient = restClient;
    this.properties = properties;
  }

  @Override
  public LlmProvider getProvider() {

    return LlmProvider.CLAUDE;
  }

  @Override
  public String generate(String systemPrompt, String userPrompt) {

    Map<String, Object> requestBody = Map.of(
        "model", properties.model(),
        "max_tokens", 1200,
        "system", systemPrompt,
        "messages", List.of(
            Map.of(
                "role", "user",
                "content", userPrompt
            )
        )
    );

    Map<?, ?> response = restClient.post()
        .uri(properties.url())
        .contentType(MediaType.APPLICATION_JSON)
        .header("x-api-key", properties.key())
        .header("anthropic-version", properties.version())
        .body(requestBody)
        .retrieve()
        .body(Map.class);

    if (response == null || !response.containsKey("content")) {
      throw new BadRequestException("Empty response from Claude API");
    }

    Object contentObj = response.get("content");
    if (!(contentObj instanceof List<?> contentList) || contentList.isEmpty()) {
      throw new BadRequestException("Claude API returned no content");
    }

    Object firstBlock = contentList.get(0);
    if (!(firstBlock instanceof Map<?, ?> firstBlockMap)) {
      throw new BadRequestException("Unexpected Claude API content format");
    }

    Object text = firstBlockMap.get("text");
    if (!(text instanceof String textValue) || textValue.isBlank()) {
      throw new BadRequestException("Claude API returned empty text block");
    }

    return textValue;
  }
}

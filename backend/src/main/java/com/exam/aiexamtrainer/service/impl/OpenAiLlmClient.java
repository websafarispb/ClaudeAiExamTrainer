package com.exam.aiexamtrainer.service.impl;

import com.exam.aiexamtrainer.config.OpenAiProperties;
import com.exam.aiexamtrainer.enums.LlmProvider;
import com.exam.aiexamtrainer.service.LlmClient;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "openai.api", name = "key")
public class OpenAiLlmClient implements LlmClient {

  private final OpenAiProperties openAiProperties;
  private final RestClient restClient = RestClient.builder().build();

  @Override
  public LlmProvider getProvider() {
    return LlmProvider.CHATGPT;
  }

  @Override
  public String generate(String systemPrompt, String userPrompt) {
    ChatCompletionRequest request = new ChatCompletionRequest(
        openAiProperties.model(),
        List.of(
            new Message("system", systemPrompt),
            new Message("user", userPrompt)
        )
    );

    try {
      ChatCompletionResponse response = restClient.post()
          .uri(openAiProperties.url())
          .header("Authorization", "Bearer " + openAiProperties.key())
          .header("Content-Type", "application/json")
          .body(request)
          .retrieve()
          .body(ChatCompletionResponse.class);

      if (response == null || response.choices() == null || response.choices().isEmpty()) {
        throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Empty response from OpenAI");
      }

      Choice firstChoice = response.choices().get(0);
      if (firstChoice.message() == null
          || firstChoice.message().content() == null
          || firstChoice.message().content().isBlank()) {
        throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "OpenAI response does not contain content");
      }

      return firstChoice.message().content();
    } catch (HttpClientErrorException.TooManyRequests ex) {
      throw new ResponseStatusException(
          HttpStatus.TOO_MANY_REQUESTS,
          "ChatGPT is temporarily unavailable: OpenAI API quota exceeded. Please switch to Claude or add billing."
      );
    } catch (HttpClientErrorException.Unauthorized ex) {
      throw new ResponseStatusException(
          HttpStatus.UNAUTHORIZED,
          "ChatGPT is unavailable: invalid or missing OpenAI API key."
      );
    } catch (HttpClientErrorException ex) {
      throw new ResponseStatusException(
          HttpStatus.BAD_GATEWAY,
          "OpenAI request failed: " + ex.getStatusCode().value()
      );
    }
  }

  public record ChatCompletionRequest(String model, List<Message> messages) {}
  public record Message(String role, String content) {}
  public record ChatCompletionResponse(List<Choice> choices) {}
  public record Choice(Message message) {}
}
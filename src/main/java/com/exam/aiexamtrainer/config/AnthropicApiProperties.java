package com.exam.aiexamtrainer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "anthropic.api")
public record AnthropicApiProperties(
    String key,
    String url,
    String model,
    String version
) {

}
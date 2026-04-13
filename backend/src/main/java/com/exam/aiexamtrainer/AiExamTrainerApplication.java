package com.exam.aiexamtrainer;

import com.exam.aiexamtrainer.config.AiSecurityProperties;
import com.exam.aiexamtrainer.config.AnthropicApiProperties;
import com.exam.aiexamtrainer.config.OpenAiProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({AiSecurityProperties.class, OpenAiProperties.class, AnthropicApiProperties.class})
public class AiExamTrainerApplication {

  public static void main(String[] args) {

    SpringApplication.run(AiExamTrainerApplication.class, args);
  }
}
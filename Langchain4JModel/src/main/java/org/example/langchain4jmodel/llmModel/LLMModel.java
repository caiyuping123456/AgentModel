package org.example.langchain4jmodel.llmModel;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import jakarta.annotation.Resource;
import lombok.Data;
import org.example.langchain4jmodel.config.NvidiaModelConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * @author caiyuping
 * @date 2026/3/3 16:45
 * @description: 创建模型
 */
@Configuration
public class LLMModel {
    @Resource
    private NvidiaModelConfig nvidiaModelConfig;

    @Bean
    public OpenAiChatModel openAiChatModel() {
        return OpenAiChatModel.builder()
                .baseUrl(nvidiaModelConfig.getBaseUrl())
                .apiKey(nvidiaModelConfig.getApiKey())
                .modelName(nvidiaModelConfig.getModel())
                .build();
    }

    @Bean
    public OpenAiStreamingChatModel openAiStreamingChatModel(){
        return OpenAiStreamingChatModel.builder()
                .baseUrl(nvidiaModelConfig.getBaseUrl())
                .apiKey(nvidiaModelConfig.getApiKey())
                .modelName(nvidiaModelConfig.getModel())
                .build();
    }
}

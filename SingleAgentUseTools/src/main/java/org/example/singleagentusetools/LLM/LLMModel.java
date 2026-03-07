package org.example.singleagentusetools.LLM;

import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import jakarta.annotation.Resource;
import org.example.singleagentusetools.config.NvidiaModelConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author caiyuping
 * @date 2026/3/9 14:04
 * @description: 大模型Bean
 */
@Configuration
public class LLMModel {

    @Resource
    private NvidiaModelConfig nvidiaModelConfig;


    /**
     * 简单对话模型
     * @return
     */
    @Bean
    public OpenAiChatModel openAiChatModel() {
        return OpenAiChatModel.builder()
                .baseUrl(nvidiaModelConfig.getBaseUrl())
                .apiKey(nvidiaModelConfig.getApiKey())
                .modelName(nvidiaModelConfig.getModel())
                .build();
    }

    /**
     * 流式对话模型
     * @return
     */
    @Bean
    public OpenAiStreamingChatModel openAiStreamingChatModel(){
        return OpenAiStreamingChatModel.builder()
                .baseUrl(nvidiaModelConfig.getBaseUrl())
                .apiKey(nvidiaModelConfig.getApiKey())
                .modelName(nvidiaModelConfig.getModel())
                .build();
    }
}

package org.example.langchain4jmodel.agent;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.tool.ToolProvider;
import jakarta.annotation.Resource;
import org.example.langchain4jmodel.agent.weather.WeatherAgent;
import org.example.langchain4jmodel.llmModel.LLMModel;
import org.example.langchain4jmodel.tools.WeatherTools;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author caiyuping
 * @date 2026/3/3 20:44
 * @description: 业务
 */
@Configuration
public class AgentFactory {

    @Resource
    private OpenAiChatModel openAiChatModel;

    @Bean
    public WeatherAgent getWeatherAgent(WeatherTools weatherTools){
        return AiServices.builder(WeatherAgent.class)
                .toolProvider(weatherTools)
                .chatModel(openAiChatModel)
                .build();
    }

}

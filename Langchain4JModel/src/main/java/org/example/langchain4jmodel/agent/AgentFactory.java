package org.example.langchain4jmodel.agent;

import dev.langchain4j.agentic.AgenticServices;
import dev.langchain4j.model.openai.OpenAiChatModel;
import jakarta.annotation.Resource;
import org.example.langchain4jmodel.agent.agentengine.MasterAgent;
import org.example.langchain4jmodel.agent.chathistory.HistoryAgent;
import org.example.langchain4jmodel.agent.weather.WeatherAgent;
import org.example.langchain4jmodel.tools.HistoryTools;
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
        return AgenticServices
                .agentBuilder(WeatherAgent.class)
                .chatModel(openAiChatModel)
                .tools(weatherTools)
                .build();
    }

    @Bean
    public HistoryAgent getHistoryAgent(HistoryTools historyTools){
        return AgenticServices
                .agentBuilder(HistoryAgent.class)
                .chatModel(openAiChatModel)
                .tools(historyTools)
                .build();
    }

    @Bean
    public MasterAgent masterAgent(WeatherAgent weatherAgent, HistoryAgent historyAgent) {
        return AgenticServices
                .supervisorBuilder(MasterAgent.class)
                .chatModel(openAiChatModel)
                .subAgents(historyAgent, weatherAgent)
                .build();
    }

}

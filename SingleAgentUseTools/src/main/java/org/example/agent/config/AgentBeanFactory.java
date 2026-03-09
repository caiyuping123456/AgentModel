package org.example.agent.config;

import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.example.agent.Agent.chatAgent;
import org.example.agent.Agent.chatAgentStream;
import org.example.agent.service.ToolService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author caiyuping
 * @date 2026/3/9 14:12
 * @description: Agent的BeanFactory工厂
 */
@Configuration
public class AgentBeanFactory {

    @Resource
    private OpenAiChatModel llm;

    @Resource
    private OpenAiStreamingChatModel openAiStreamingChatModel;

    @Resource
    private ToolService toolService;

    @Bean
    public chatAgent getChatAgent(){
        return AiServices.builder(chatAgent.class)
                .chatModel(llm)
                .tools(toolService.getAllToolProvider())
                .build();
    }

    @Bean
    public chatAgentStream getChatAgentStream(){
        return AiServices.builder(chatAgentStream.class)
                .streamingChatModel(openAiStreamingChatModel)
                .tools(toolService.getAllToolProvider())
                .build();
    }
}

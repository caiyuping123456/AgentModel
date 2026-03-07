package org.example.singleagentusetools.config;

import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.example.singleagentusetools.Agent.chatAgent;
import org.example.singleagentusetools.Agent.chatAgentStream;
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

    @Bean
    public chatAgent getChatAgent(){
        return AiServices.builder(chatAgent.class)
                .chatModel(llm)
                .build();
    }

    @Bean
    public chatAgentStream getChatAgentStream(){
        return AiServices.builder(chatAgentStream.class)
                .streamingChatModel(openAiStreamingChatModel)
                .build();
    }
}

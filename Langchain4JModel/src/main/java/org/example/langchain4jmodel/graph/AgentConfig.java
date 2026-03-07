package org.example.langchain4jmodel.graph;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.memory.ChatMemoryService;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import org.example.langchain4jmodel.agent.chathistory.HistoryAgent;
import org.example.langchain4jmodel.agent.search.SearchAgent;
import org.example.langchain4jmodel.agent.weather.WeatherAgent;
import org.example.langchain4jmodel.graph.WrapperNode.GraphNode;
import org.example.langchain4jmodel.graph.WrapperNode.SaveMsgNode;
import org.example.langchain4jmodel.graph.WrapperNode.SearchNode;
import org.example.langchain4jmodel.graph.WrapperNode.WeatherNode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author caiyuping
 * @date 2026/3/6 16:23
 * @description: 组装Agent的工作流
 */
@Configuration
public class AgentConfig {

    private final ChatMemoryStore chatMemoryStore;
    private final OpenAiChatModel chatModel;

    // 注入自定义的 Store
    public AgentConfig(ChatMemoryStore chatMemoryStore, OpenAiChatModel chatModel) {
        this.chatMemoryStore = chatMemoryStore;
        this.chatModel = chatModel;
    }

    /**
     * 这个是注册Agent的记忆容器
     * @return
     */
    @Bean
    public ChatMemoryService chatMemoryService() {
        return new ChatMemoryService(memoryId ->
                MessageWindowChatMemory.builder()
                        .id(memoryId)
                        .chatMemoryStore(chatMemoryStore)
                        .maxMessages(10) // 最多保留 10 条消息
                        .build()
        );
    }

//    /**
//     * 这个是节点Bean
//     * @param weatherAgent
//     * @return
//     */
//    @Bean
//    public WeatherNode weatherNode(WeatherAgent weatherAgent) {
//        return new WeatherNode(weatherAgent);
//    }
//
//    /**
//     * 这个也是节点Bean
//     * @param searchAgent
//     * @return
//     */
//    @Bean
//    public SearchNode searchNode(SearchAgent searchAgent) {
//        return new SearchNode(searchAgent);
//    }
//
//    @Bean
//    public SaveMsgNode saveMsgNode(HistoryAgent historyAgent){
//        return new SaveMsgNode(historyAgent);
//    }

    /**
     * 编排器 Bean
     * @param nodes
     * @param chatModel
     * @param chatMemoryService
     * @return
     */
    @Bean
    public OrchestratorAgent orchestratorAgent(
            List<GraphNode> nodes,
            OpenAiChatModel chatModel,
            ChatMemoryService chatMemoryService) {

        OrchestratorAgent orchestrator = new OrchestratorAgent(nodes, chatModel, chatMemoryService);
        // 可以在这里设置 systemPrompt
        orchestrator.setSystemPrompt("You are a helpful assistant routing tasks.");
        return orchestrator;
    }

}
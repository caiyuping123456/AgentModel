package org.example.langchain4jmodel.graph;

import dev.langchain4j.agentic.AgenticServices;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.memory.chat.TokenWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.memory.ChatMemoryService;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import jakarta.annotation.Resource;
import org.example.langchain4jmodel.agent.chathistory.HistoryAgent;
import org.example.langchain4jmodel.agent.search.SearchAgent;
import org.example.langchain4jmodel.agent.weather.WeatherAgent;
import org.example.langchain4jmodel.graph.WrapperNode.SearchNode;
import org.example.langchain4jmodel.graph.WrapperNode.WeatherNode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author caiyuping
 * @date 2026/3/6 16:23
 * @description: 业务
 */
@Configuration
public class AgentConfig {

    private final ChatMemoryStore chatMemoryStore;
    private final ChatModel chatModel;

    // 注入自定义的 Store
    public AgentConfig(ChatMemoryStore chatMemoryStore, ChatModel chatModel) {
        this.chatMemoryStore = chatMemoryStore;
        this.chatModel = chatModel;
    }

    // 1. 创建 ChatMemoryService Bean (核心管理器)
    @Bean
    public ChatMemoryService chatMemoryService() {
        return new ChatMemoryService(memoryId ->
                MessageWindowChatMemory.builder()
                        .id(memoryId)
                        .chatMemoryStore(chatMemoryStore)
                        .maxMessages(20) // 最多保留 20 条消息
                        .build()
        );
    }

    // 3. 节点 Beans
    @Bean
    public WeatherNode weatherNode(WeatherAgent weatherAgent) {
        return new WeatherNode(weatherAgent);
    }

    @Bean
    public SearchNode searchNode(SearchAgent searchAgent) {
        return new SearchNode(searchAgent);
    }

    // 4. 编排器 Bean (注入 ChatMemoryService 替代 CheckpointRepository)
    @Bean
    public OrchestratorAgent orchestratorAgent(
            List<GraphNode> nodes,
            ChatModel chatModel,
            ChatMemoryService chatMemoryService) { // <--- 关键变化

        OrchestratorAgent orchestrator = new OrchestratorAgent(nodes, chatModel, chatMemoryService);
        // 可以在这里设置 systemPrompt
        orchestrator.setSystemPrompt("You are a helpful assistant routing tasks.");
        return orchestrator;
    }

}
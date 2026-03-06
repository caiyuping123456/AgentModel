package org.example.langchain4jmodel.graph;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.memory.ChatMemoryService;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author caiyuping
 * @date 2026/3/6 16:22
 * @description: 业务
 */
@Slf4j
@Component
@ConfigurationProperties(prefix = "agent.orchestrator")
@Data
public class OrchestratorAgent {

    private List<GraphNode> allNodes = new ArrayList<>();
    private ChatModel chatModel;
    private ChatMemoryService chatMemoryService;

    private String systemPrompt = "You are a helpful assistant."; // 给个默认值

    private Map<String, GraphNode> nodeMap;
    private static final Map<String, AgentState> metaDataStore = new ConcurrentHashMap<>();
    public OrchestratorAgent(List<GraphNode> allNodes, ChatModel chatModel, ChatMemoryService chatMemoryService) {
        this.allNodes = allNodes;
        this.chatModel = chatModel;
        this.chatMemoryService = chatMemoryService;
        init(); // 在构造时初始化 nodeMap
    }

    // 无参构造函数（可选，如果不需要可以删掉）
    public OrchestratorAgent() {}

    @PostConstruct
    public void init() {
        if (allNodes != null) {
            nodeMap = allNodes.stream()
                    .collect(Collectors.toMap(GraphNode::getName, n -> n));
        } else {
            nodeMap = Map.of();
        }
        log.info("OrchestratorAgent initialized with {} nodes", nodeMap.size());
    }

    /**
     * 核心执行方法
     */
    public AgentState run(String sessionId, String userInput) {
        // 1. 【获取 ChatMemory】
        // 这会自动从 ChatMemoryStore 加载历史消息，并应用淘汰策略
        final InMemoryChatMemoryStore globalStore = new InMemoryChatMemoryStore();

        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .id(sessionId)
                .chatMemoryStore(globalStore)
                .maxMessages(100) // 保留最近 100 条消息，防止上下文过长
                .build();

        // 2. 【加载元数据状态】
        // 只加载流程控制数据 (nextNode, variables 等)，不包含 messages
        AgentState state = metaDataStore.get(sessionId);
        if (state == null) {
            state = new AgentState();
            state.setSessionId(sessionId);
            state.setFinished(false);
            state.setNextNode("__START__");
        }

        // 3. 【添加用户输入】
        // 直接加入 ChatMemory，而不是 state.messages
        if (userInput != null && !userInput.trim().isEmpty()) {
            chatMemory.add(new UserMessage(userInput));
            log.info("👤 User input added to memory: {}", userInput);
        }

        int stepCount = 0;
        int maxSteps = 15;

        log.info("Starting workflow loop for session: {}", sessionId);

        // 4. 【循环工作流】
        while (!state.isFinished() && stepCount < maxSteps) {

            // --- 路由决策 ---
            String currentNode = state.getNextNode();
            if (currentNode == null || "__DECIDE__".equals(currentNode) || "__START__".equals(currentNode)) {
                // 决策时需要看到完整的历史上下文
                state.setNextNode(decideNextNode(chatMemory.messages(), state));
                log.info("Router decided next node: {}", state.getNextNode());

                // 如果决策后还是 __END__ (比如空输入)，直接结束
                if ("__END__".equals(state.getNextNode())) {
                    state.setFinished(true);
                    break;
                }
            }

            String nodeName = state.getNextNode();

            if ("__END__".equals(nodeName)) {
                state.setFinished(true);
                break;
            }

            GraphNode node = nodeMap.get(nodeName);
            if (node == null) {
                throw new IllegalStateException("Unknown node: " + nodeName);
            }

            // --- 人类介入检查 (Interrupt Before) ---
            if (shouldInterrupt(nodeName)) {
                saveMetaData(sessionId, state); // 保存元数据
                log.warn("Interrupted before node: {}. Waiting for human approval.", nodeName);
                return state; // 退出，等待下一次调用 (可能带 approval 信号)
            }

            // --- 执行节点 ---
            log.info("Executing node: {}", nodeName);
            state.setCurrentStep(nodeName);

            // 关键：将 chatMemory 传递给节点
            state = node.process(state, chatMemory);

            // 重置 nextNode，迫使下一轮重新决策 (除非节点内部显式指定了下一步)
            // 如果你的节点逻辑里已经确定了下一步，可以在 process 里设置 state.setNextNode(...)
            if ("__DECIDE__".equals(state.getNextNode()) || state.getNextNode() == null) {
                state.setNextNode("__DECIDE__");
            }

            // --- 保存元数据 ---
            // 注意：消息数据已经在 chatMemory.add() 时自动通过 ChatMemoryStore 持久化了
            // 这里只需要保存 variables, nextNode, finished 等轻量级状态
            saveMetaData(sessionId, state);

            stepCount++;
        }

        if (stepCount >= maxSteps) {
            log.error("Workflow exceeded max steps ({}) for session: {}", maxSteps, sessionId);
            state.setFinished(true);
        }

        return state;
    }

    /**
     * 路由决策逻辑
     */
    private String decideNextNode(List<dev.langchain4j.data.message.ChatMessage> history, AgentState state) {
        // 构建 Prompt，包含历史记录
        // 这里可以使用一个简单的 PromptTemplate + chatModel.generate()
        // 伪代码示例：
        /*
        String prompt = systemPrompt + "\nHistory:\n" + history.toString() + "\nDecide next node:";
        String response = chatModel.generate(prompt);
        return parseResponse(response);
        */

        // 简化演示：如果包含"天气"关键词去 weather_node，否则去 search_node
        String lastText = history.stream()
                .filter(m -> m instanceof UserMessage)
                .map(m -> ((UserMessage)m).singleText())
                .reduce((a,b)->b).orElse("");

        if (lastText.contains("天气") || lastText.contains("weather")) {
            return "weather_node";
        } else {
            return "search_node";
        }
    }

    private boolean shouldInterrupt(String nodeName) {
        // 配置中断逻辑，例如支付节点需要人工确认
        return "payment_node".equals(nodeName);
    }

    // --- 辅助方法：模拟元数据持久化 ---
    private void saveMetaData(String sessionId, AgentState state) {
        metaDataStore.put(sessionId, state);
        // 生产环境：redisTemplate.opsForHash().put("agent:meta:" + sessionId, "data", toJson(state));
    }

    // 用于测试清理
    public static void clearStaticStore() {
        metaDataStore.clear();
    }
}
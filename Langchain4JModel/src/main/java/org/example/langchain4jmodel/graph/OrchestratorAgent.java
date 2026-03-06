package org.example.langchain4jmodel.graph;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.memory.ChatMemoryService;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.example.langchain4jmodel.graph.WrapperNode.GraphNode;
import org.springframework.beans.factory.annotation.Value;
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
 * @description: 多 Agent 编排器 (Orchestrator) - 包含路由决策与最终结果汇总
 */
@Slf4j
@Component
@ConfigurationProperties(prefix = "agent.orchestrator")
@Data
public class OrchestratorAgent {

    /**
     * 所有可用的节点 (由 Spring 自动注入)
     */
    private List<GraphNode> allNodes = new ArrayList<>();

    /**
     * 大模型 (用于路由决策和最终汇总)
     */
    private OpenAiChatModel chatModel;

    /**
     * 记忆服务 (注入用，当前逻辑主要使用局部 Store 演示)
     */
    private ChatMemoryService chatMemoryService;

    /**
     * 系统提示词
     * 修复：增加 ${} 占位符语法和默认值，防止配置缺失时报错
     */
    @Value("${agent.orchestrator.system-message:You are a helpful assistant.}")
    private String systemPrompt = "You are a helpful assistant.";

    /**
     * 节点地图：名字 -> 对象，方便快速查找
     */
    private Map<String, GraphNode> nodeMap;

    /**
     * 静态存储：存流程状态（非消息）
     * 注意：生产环境建议使用 Redis 等外部存储替代 ConcurrentHashMap
     */
    private static final Map<String, AgentState> metaDataStore = new ConcurrentHashMap<>();

    public OrchestratorAgent(List<GraphNode> allNodes, OpenAiChatModel chatModel, ChatMemoryService chatMemoryService) {
        this.allNodes = allNodes;
        this.chatModel = chatModel;
        this.chatMemoryService = chatMemoryService;
        init(); // 在构造时初始化 nodeMap
    }

    public OrchestratorAgent() {}

    /**
     * 把所有注入进来的节点（WeatherNode, SearchNode...）整理成字典
     */
    @PostConstruct
    public void init() {
        if (allNodes != null) {
            nodeMap = allNodes.stream()
                    .collect(Collectors.toMap(GraphNode::getName, n -> n));
        } else {
            nodeMap = Map.of();
        }
        log.info("Orchestrator 加载到了 {} 个节点", nodeMap.size());
    }

    /**
     * 核心执行方法
     * @param sessionId 会话ID
     * @param userInput 用户输入
     * @return 最终的状态对象
     */
    public AgentState run(String sessionId, String userInput) {
        // ⚠️ 注意：这里的 InMemoryChatMemoryStore 是局部变量。
        // 这意味着每次 run() 调用都会创建一个新的空存储。
        // 如果是真正的多轮对话场景，请将 globalStore 提升为成员变量或使用 Spring Bean 管理的单例 Store。
        // 当前写法适用于“单次请求完成整个工作流”的场景。
        final InMemoryChatMemoryStore globalStore = new InMemoryChatMemoryStore();

        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .id(sessionId)
                .chatMemoryStore(globalStore)
                .maxMessages(100) // 保留最近 100 条消息
                .build();

        // 加载流程控制数据 (nextNode, variables 等)
        AgentState state = metaDataStore.get(sessionId);
        if (state == null) {
            state = new AgentState();
            state.setSessionId(sessionId);
            state.setFinished(false);
            state.setNextNode("__START__");
        }

        // 添加用户输入到记忆
        if (userInput != null && !userInput.trim().isEmpty()) {
            chatMemory.add(new UserMessage(userInput));
            log.info("用户输入已存入内存 [{}]: {}", sessionId, userInput);
        }

        int stepCount = 0;
        int maxSteps = 15;

        log.info("开始执行会话 ID: {}", sessionId);

        // --- 1. 核心工作流循环 ---
        while (!state.isFinished() && stepCount < maxSteps) {
            String currentNode = state.getNextNode();

            // 路由决策：如果当前是 START, DECIDE 或 null，则让 LLM 决定下一步
            if (currentNode == null || "__DECIDE__".equals(currentNode) || "__START__".equals(currentNode)) {
                state.setNextNode(decideNextNode(chatMemory.messages(), state));
                log.info("Router 决策下一个节点: {}", state.getNextNode());

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
                throw new IllegalStateException("未知节点: " + nodeName);
            }

            // 人类介入检查 (例如支付确认)
            if (shouldInterrupt(nodeName)) {
                saveMetaData(sessionId, state);
                log.warn("流程中断，等待人工确认: {}", nodeName);
                return state;
            }

            log.info("执行节点: {}", nodeName);
            state.setCurrentStep(nodeName);

            // 执行节点逻辑 (子 Agent 会更新 state 中的 variables 和 chatMemory)
            state = node.process(state, chatMemory);

            // 如果节点没有显式指定下一步，默认回到决策状态
            if ("__DECIDE__".equals(state.getNextNode()) || state.getNextNode() == null) {
                state.setNextNode("__DECIDE__");
            }

            // 保存元数据 (variables, nextNode, finished 等)
            saveMetaData(sessionId, state);

            stepCount++;
        }

        if (stepCount >= maxSteps) {
            log.error("会话 {} 超过最大步数限制 ({})", sessionId, maxSteps);
            state.setFinished(true);
        }

        // --- 2. 流程结束后的汇总 (调用封装好的方法) ---
        if (state.isFinished()) {
            synthesizeFinalResponse(sessionId, state, chatMemory);
        }

        return state;
    }

    /**
     * ✨ 封装方法：合成最终回复
     * 负责收集所有子节点的执行结果，调用主 LLM 生成一段自然流畅的总结，并写入记忆。
     */
    private void synthesizeFinalResponse(String sessionId, AgentState state, ChatMemory chatMemory) {
        log.info("[汇总阶段] 正在为会话 {} 生成最终回复...", sessionId);

        // 1. 收集上下文数据
        StringBuilder contextBuilder = new StringBuilder();
        contextBuilder.append("【执行结果摘要】\n");

        if (state.getVariables() != null && !state.getVariables().isEmpty()) {
            for (Map.Entry<String, Object> entry : state.getVariables().entrySet()) {
                // 跳过内部标记变量 (以下划线开头)，只展示业务结果
                if (!entry.getKey().startsWith("_")) {
                    contextBuilder.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
                }
            }
        } else {
            contextBuilder.append("- 无结构化数据输出。\n");
        }

        // 获取用户原始请求作为参考
        String originalRequest = chatMemory.messages().stream()
                .filter(m -> m instanceof UserMessage)
                .map(m -> ((UserMessage) m).singleText())
                .reduce((first, second) -> second)
                .orElse("未知请求");

        // 2. 构建 Prompt
        String summaryPrompt = String.format(
                "System: 你是主调度助手。你的任务是根据下方的【执行结果摘要】，为用户生成一段自然、流畅、友好的最终回复。\n" +
                        "要求：\n" +
                        "1. 不要提及内部节点名称（如 'weather_node'），直接说结果。\n" +
                        "2. 如果结果中包含“保存成功”等后台操作，请自然地告知用户“已为您记录/保存”。\n" +
                        "3. 语气要专业且亲切。\n" +
                        "4. 如果没有任何有效结果，请礼貌地告知用户无法完成任务。\n\n" +
                        "用户原始请求：%s\n\n" +
                        "%s\n\n" +
                        "请输出最终回复（不要包含其他多余内容）：",
                originalRequest,
                contextBuilder.toString()
        );

        try {
            // 3. 调用 LLM 生成总结
            String finalResponse = chatModel.chat(summaryPrompt);

            log.info("[汇总阶段] 生成的最终回复:\n{}", finalResponse);

            // 4. 将回复写入 ChatMemory (作为最后一条 AI 消息)
            chatMemory.add(new AiMessage(finalResponse));

            // 5. 同时更新 State，方便外部直接获取
            state.setVariable("final_summary", finalResponse);

        } catch (Exception e) {
            log.error("[汇总阶段] 生成最终回复失败", e);
            // 降级处理
            String fallback = "任务已执行完毕。如有其他问题，请随时问我。";
            chatMemory.add(new AiMessage(fallback));
            state.setVariable("final_summary", fallback);
        }
    }

    /**
     * 路由决策逻辑
     */
    private String decideNextNode(List<dev.langchain4j.data.message.ChatMessage> history, AgentState state) {
        if (nodeMap == null || nodeMap.isEmpty()) {
            return "__END__";
        }

        // 1. 构建节点列表 (纯文本)
        String availableNodes = nodeMap.values().stream()
                .map(node -> node.getName() + ":" + node.getDescription())
                .collect(Collectors.joining("\n"));

        // 2. 构建简短的历史记录 (只取最近 6 条)
        int startIdx = Math.max(0, history.size() - 6);
        List<dev.langchain4j.data.message.ChatMessage> recentHistory = history.subList(startIdx, history.size());

        String historyText = recentHistory.stream()
                .map(m -> {
                    if (m instanceof UserMessage um) return "User: " + um.singleText();
                    if (m instanceof AiMessage ai) return "Assistant: " + ai.text();
                    return "";
                })
                .collect(Collectors.joining("\n"));

        // 3. 构建强约束 Prompt
        String prompt = String.format(
                "System: You are a strict router API. Output ONLY the node name or __END__.\n" +
                        "Available Nodes:\n%s\n\n" +
                        "Conversation:\n%s\n\n" +
                        "Rule: If the last Assistant message already answers the User's latest request, output '__END__'.\n" +
                        "Rule: Otherwise, output exactly one node name from the list above.\n" +
                        "Rule: NO explanations, NO punctuation, NO markdown.\n" +
                        "Output:",
                availableNodes,
                historyText
        );

        // 4. 调用 LLM
        String rawResponse;
        try {
            rawResponse = chatModel.chat(prompt);
        } catch (Exception e) {
            log.error("LLM 路由调用失败", e);
            return "__END__";
        }

        log.debug("LLM 原始路由响应: [{}]", rawResponse);
        String lowerResponse = rawResponse.toLowerCase();

        // A. 尝试从废话中提取节点名
        for (String validNode : nodeMap.keySet()) {
            if (lowerResponse.contains(validNode.toLowerCase())) {
                log.info("从响应中提取到节点: {}", validNode);
                return validNode;
            }
        }

        // B. 检查是否包含结束指令
        if (lowerResponse.contains("__end__") || lowerResponse.contains("end") || lowerResponse.contains("finish")) {
            return "__END__";
        }

        // C. 安全熔断：如果上一条是 AI 消息，强制结束，防止死循环
        if (!recentHistory.isEmpty() && recentHistory.get(recentHistory.size() - 1) instanceof AiMessage) {
            log.warn("安全熔断：LLM 响应无效且上一条为 AI 消息，强制结束。响应片段: {}", rawResponse.substring(0, Math.min(50, rawResponse.length())));
            return "__END__";
        }

        // D. Fallback (仅在最后一条是用户消息时)
        log.warn("LLM 未能选择有效节点: {}. 默认 fallback 到 search_node", rawResponse);
        return "search_node";
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
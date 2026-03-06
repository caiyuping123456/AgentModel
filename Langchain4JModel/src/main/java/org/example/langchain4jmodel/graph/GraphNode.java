package org.example.langchain4jmodel.graph;

/**
 * @author caiyuping
 * @date 2026/3/6 16:13
 * @description: 业务
 */

import dev.langchain4j.memory.ChatMemory;

/**
 * 图节点接口
 */
public interface GraphNode {
    String getName();

    /**
     * 处理逻辑
     * @param state 流程状态 (不含消息)
     * @param chatMemory 当前会话的记忆对象 (用于读写消息)
     * @return 更新后的状态
     */
    AgentState process(AgentState state, ChatMemory chatMemory);
}
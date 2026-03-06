package org.example.langchain4jmodel.graph.WrapperNode;

/**
 * @author caiyuping
 * @date 2026/3/6 16:13
 * @description: 业务
 */

import dev.langchain4j.memory.ChatMemory;
import org.example.langchain4jmodel.graph.AgentState;

/**
 * 图节点接口
 */
public interface GraphNode {

    /**
     * 获取到某一个节点的名称
     * @return
     */
    String getName();

    String getDescription();

    /**
     * 处理逻辑
     * @param state 流程状态 (不含消息)
     * @param chatMemory 当前会话的记忆对象 (用于读写消息)
     * @return 更新后的状态
     */
    AgentState process(AgentState state, ChatMemory chatMemory);
}
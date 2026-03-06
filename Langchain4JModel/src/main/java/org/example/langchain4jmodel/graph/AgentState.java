package org.example.langchain4jmodel.graph;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author caiyuping
 * @date 2026/3/6 16:09
 * @description: 定义共享状态对象 (仅包含流程控制数据，不包含消息历史)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgentState {
    /**
     * 会话ID (用于关联 ChatMemory)
     */
    private String sessionId;

    /**
     * 临时变量 (如搜索到的结果、中间数据)
     */
    private Map<String, Object> variables;

    /**
     * 当前所在节点名称 (可选，主要用于调试)
     */
    private String currentStep;

    /**
     * 是否结束
     */
    private boolean finished;

    /**
     * 下一个要执行的节点名称
     * 特殊值: "__START__", "__DECIDE__", "__END__"
     */
    private String nextNode;

    /**
     * 缓存临时变量
     */
    public void setVariable(String key, Object value) {
        if (this.variables == null) this.variables = new HashMap<>();
        this.variables.put(key, value);
    }

    public Object getVariable(String key) {
        return this.variables != null ? this.variables.get(key) : null;
    }
}
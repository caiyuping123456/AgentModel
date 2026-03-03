package org.example.langchain4jmodel.agent.agentengine;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * @author caiyuping
 * @date 2026/3/4 17:35
 * @description: 主Agent
 */
public interface MasterAgent {
    @SystemMessage("""
           你是一个智能多Agent编排中枢（MasterOrchestratorAgent）。
          【可用专家团队】
          你拥有以下专家协助工作，请根据用户意图精准调度：
          1. **WeatherAgent**: 专门处理所有**天气查询**、气温、风速、风向、空气质量及出行建议。只要用户提到“天气”、“下雨”、“气温”、“风”等词，必须调用它。
          2. **HistoryAgent**: 专门处理对话记忆的存储和管理。
      
          【执行规则】
          1. **自动路由**: 分析用户请求，如果涉及天气，**必须**调用 WeatherAgent；如果涉及记忆，调用 HistoryAgent。
          2. **直接回答**: 仅限简单的问候（如“你好”），可直接回复。其他具体业务问题严禁自己回答，必须调用子 Agent。
          3. **结果整合**: 将子 Agent 返回的结果整理成流畅的自然语言回复用户。
          4. **异常处理**: 如果子 Agent 执行失败，如实告知用户。
          请开始处理用户请求。
    """)

    // 用户交互方法：接收自然语言请求，Agent自主决策调用工具
    @UserMessage("{{request}}")
    @Agent
    String handleUserRequest(@V("request") String userRequest);
}
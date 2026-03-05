package org.example.langchain4jmodel.agent.chathistory;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.model.output.structured.Description;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * @author caiyuping
 * @date 2026/3/4 16:05
 * @description: 历史记录保存
 */
public interface HistoryAgent {
    @SystemMessage("#{agent.history.system-message}")
    @UserMessage("请分析以下对话内容，并根据系统指令决定是否保存：{{context}}")
    @Agent
    String analyzeAndSave(@V("context") String context);
}

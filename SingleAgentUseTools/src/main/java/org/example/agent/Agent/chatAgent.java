package org.example.agent.Agent;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * @author caiyuping
 * @date 2026/3/9 14:10
 * @description: 这个是唯一的Agent
 */
public interface chatAgent {
    /**
     * 对话模型
     * @param userRequest
     * @return
     */
    @UserMessage("{{request}}")
    String chat(@V("request") String userRequest);
}

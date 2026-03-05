package org.example.langchain4jmodel.agent.agentengine;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * @author caiyuping
 * @date 2026/3/4 17:35
 * @description: 主Agent - 智能多Agent编排中枢
 */
public interface MasterAgent {

    @SystemMessage("#{agent.master.system-message}")
    @UserMessage("{{request}}")
    @Agent
    String handleUserRequest(@V("request") String userRequest);
}
package org.example.langchain4jmodel.agent.search;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * @author caiyuping
 * @date 2026/3/6 16:48
 * @description: 业务
 */
public interface SearchAgent {
    @SystemMessage("#{agent.search.system-message}")
    @UserMessage("这个是用户的提问：{{msg}}，请按这个提问回复用户")
    @Agent
    String chat(@V("msg") String msg);
}
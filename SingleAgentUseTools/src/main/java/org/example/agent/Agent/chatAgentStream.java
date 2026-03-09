package org.example.agent.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * @author caiyuping
 * @date 2026/3/9 14:37
 * @description: 流式模型
 */
public interface chatAgentStream {

    /**
     * 对话
     * @param userRequest
     * @return 返回 TokenStream 以支持流式输出
     */
    @SystemMessage("#{agent.system_prompt}")
    @UserMessage("{{request}}")
    TokenStream chat(@V("request") String userRequest);
}

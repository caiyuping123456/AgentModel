package org.example.agent.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import org.springframework.context.annotation.Configuration;

/**
 * @author caiyuping
 * @date 2026/3/9 14:37
 * @description: 流式模型
 */
@Configuration
public interface chatAgentStream {

    /**
     * 流式对话模型
     * @param userRequest
     * @return 返回 TokenStream 以支持流式输出
     */
    @UserMessage("{{request}}")
    TokenStream chat(@V("request") String userRequest);
}

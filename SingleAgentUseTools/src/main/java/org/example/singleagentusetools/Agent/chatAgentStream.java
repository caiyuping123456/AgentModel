package org.example.singleagentusetools.Agent;
import dev.langchain4j.model.chat.ChatModel;
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
    @SystemMessage("你是一个智能助手")
    @UserMessage("{{request}}")
    TokenStream chat(@V("request") String userRequest);
}

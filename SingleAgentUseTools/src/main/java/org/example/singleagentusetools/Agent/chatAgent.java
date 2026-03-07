package org.example.singleagentusetools.Agent;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * @author caiyuping
 * @date 2026/3/9 14:10
 * @description: 这个是唯一的Agent
 */
public interface chatAgent {
    /**
     * 对话
     * @param userRequest
     * @return
     */
    @SystemMessage("你是一个只能助手，简短回答就可以，尽量可以100字完成回复")
    @UserMessage("{{request}}")
    String chat(@V("request") String userRequest);
}

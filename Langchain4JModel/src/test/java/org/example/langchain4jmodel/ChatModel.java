package org.example.langchain4jmodel;

import dev.langchain4j.model.openai.OpenAiChatModel;
import jakarta.annotation.Resource;
import org.example.langchain4jmodel.llmModel.LLMModel;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author caiyuping
 * @date 2026/3/3 16:37
 * @description: 大模型LLM
 */

@SpringBootTest
public class ChatModel {
    @Resource
    private OpenAiChatModel chatModel;

    @Test
    void m1(){
        String chat = chatModel.chat("你好");
        assert !chat.isEmpty();
    }
}

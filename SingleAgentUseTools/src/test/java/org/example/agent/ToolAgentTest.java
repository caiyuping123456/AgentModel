package org.example.agent;

import jakarta.annotation.Resource;
import org.example.agent.Agent.chatAgent;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author caiyuping
 * @date 2026/3/10 21:59
 * @description: 业务
 */
@SpringBootTest
public class ToolAgentTest {
    @Resource
    private chatAgent chatAgent;

    @Test
    void m1(){
        String question = "赣州进行的天气?";
        String result = chatAgent.chat(question);
        System.out.println(result);
    }
}

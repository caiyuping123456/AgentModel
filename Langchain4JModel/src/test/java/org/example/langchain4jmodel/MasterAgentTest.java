package org.example.langchain4jmodel;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.langchain4jmodel.agent.agentengine.MasterAgent;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author caiyuping
 * @date 2026/3/4 17:43
 * @description: 业务
 */

@SpringBootTest
@Slf4j
public class MasterAgentTest {
    @Resource
    private MasterAgent masterAgent;

    @Test
    void m1() {
        String result = masterAgent.handleUserRequest("你好");
        System.out.println(result);
        assert !masterAgent.handleUserRequest("你好").isEmpty();
        assert !masterAgent.handleUserRequest("赣州天气如何").isEmpty();
    }
}

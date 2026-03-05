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
        long time1 = System.currentTimeMillis();
        String result = masterAgent.handleUserRequest("（用户的id是：123），请问南昌今天的天气？，请将这个进行存储");
        System.out.println(result);
        long time2 = System.currentTimeMillis();
        System.out.println("回答1，花费时间："+(time2-time1));
        String result2 = masterAgent.handleUserRequest("（用户的id是：123），请问南昌今天的天气？，请将这个进行存储");
        System.out.println(result2);
        long time3 = System.currentTimeMillis();
        System.out.println("回答2，花费时间："+(time3-time2));
//        assert !masterAgent.handleUserRequest("你好").isEmpty();
//        assert !masterAgent.handleUserRequest("赣州天气如何").isEmpty();
    }

    @Test
    void m2() {
        String result = masterAgent.handleUserRequest("（用户的id是：123），请问赣州今天的天气？，请将这个进行存储");
        System.out.println(result);
    }
}

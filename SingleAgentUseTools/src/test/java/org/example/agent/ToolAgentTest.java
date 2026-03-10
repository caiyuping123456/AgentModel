package org.example.agent;

import jakarta.annotation.Resource;
import org.example.agent.Agent.chatAgent;
import org.example.agent.Agent.chatAgentStream;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;

/**
 * @author caiyuping
 * @date 2026/3/10 21:59
 * @description: 业务
 */
@SpringBootTest
public class ToolAgentTest {
    @Resource
    private chatAgent chatAgent;

    @Resource
    private chatAgentStream chatAgentStream;

    @Test
    void m1(){
        String question = "赣州今天的天气?";
        String result = chatAgent.chat(question);
        System.out.println(result);
    }

    @Test
    void m2() throws InterruptedException {
        String question = "赣州今天的天气?";
        CountDownLatch countDownLatch = new CountDownLatch(1);
        chatAgentStream.chat(question).onPartialResponse(item->{
            System.out.print(item);
        }).onCompleteResponse(item->{
            System.out.println();
            countDownLatch.countDown();
        }).onError(item->{
            System.out.println("出错了，请重试");
            countDownLatch.countDown();
        }).start();

        countDownLatch.await();
    }
}

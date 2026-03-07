package org.example.singleagentusetools;

import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.service.TokenStream;
import jakarta.annotation.Resource;
import org.example.singleagentusetools.Agent.chatAgent;
import org.example.singleagentusetools.Agent.chatAgentStream;
import org.example.singleagentusetools.handler.StreamingHandler;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;

import static java.lang.Thread.sleep;

@SpringBootTest
class SingleAgentUseToolsApplicationTests {

    @Resource
    private chatAgent chatAgent;

    @Resource
    private OpenAiStreamingChatModel openAiStreamingChatModel;

    @Resource
    private chatAgentStream stream;


    @Test
    void m1(){
        String chat = chatAgent.chat("解释一下什么是大数据");
        System.out.println(chat);
    }

    @Test
    void m2() {
        String msg = "说说如何写好java代码?";
        openAiStreamingChatModel.chat(msg, new StreamingHandler(
                partialText ->{
                    for (char item : partialText.toCharArray()){
                        System.out.print(item);
                        System.out.flush();
                    }
                },
                completeResponse->{
                    System.out.println("打印完毕");
                }
        ));
        try {
            sleep(600000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void m3() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        stream.chat("解释一下什么是python")
                .onPartialResponse(token -> {
                    System.out.print(token);
                })
                .onCompleteResponse(response -> {
                    System.out.println("输出结束了");
                    latch.countDown();
                })
                .onError(error -> {
                    error.printStackTrace();
                    latch.countDown();
                })
                .start();

        latch.await();
    }
}

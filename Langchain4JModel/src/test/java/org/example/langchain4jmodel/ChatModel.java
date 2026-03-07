package org.example.langchain4jmodel;

import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import jakarta.annotation.Resource;
import org.example.langchain4jmodel.handler.StreamingHandler;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static dev.langchain4j.model.LambdaStreamingResponseHandler.onPartialResponse;
import static dev.langchain4j.model.LambdaStreamingResponseHandler.onPartialResponseAndError;
import static java.lang.Thread.sleep;

/**
 * @author caiyuping
 * @date 2026/3/3 16:37
 * @description: 大模型LLM
 */

@SpringBootTest
public class ChatModel {
    @Resource
    private OpenAiChatModel chatModel;

    @Resource
    private OpenAiStreamingChatModel openAiStreamingChatModel;

    @Test
    void m1(){
        String chat = chatModel.chat("这句话：‘用户的Id为134，今天赣州天气怎么样？请进行保存’里面有几个任务");
        System.out.println(chat);
        assert !chat.isEmpty();
    }

    @Test
    void m2() throws InterruptedException {
        String chat = "给我说一个笑话";
        System.out.println(">>> 开始生成...\n");

        // 2. 发起请求
        openAiStreamingChatModel.chat(chat, new StreamingChatResponseHandler() {

            @Override
            public void onPartialResponse(String partialResponse) {
                // 【关键步骤】：底层一次可能返回 "你好啊" (3个字)
                // 我们需要遍历这个字符串，逐个字符处理
                for (char c : partialResponse.toCharArray()) {
                    System.out.print(c);      // 打印单个字符
                    System.out.flush();       // 【必须】强制刷新控制台缓冲区，否则可能攒着不显示

                    // 【模拟打字机延迟】
                    // 如果没有这个 sleep，即使拆开了，也会瞬间打印完，看不出效果
                    try {
                        Thread.sleep(30); // 每个字停顿 30 毫秒，可根据喜好调整 (10-50ms)
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }

            @Override
            public void onCompleteResponse(ChatResponse completeResponse) {
                System.out.println("\n\n>>> 生成完毕！");
            }

            @Override
            public void onError(Throwable error) {
                System.err.println("\n>>> 出错了: " + error.getMessage());
                error.printStackTrace();
            }

            // 其他方法可以留空，除非你需要处理思考过程或工具调用
            @Override public void onPartialThinking(dev.langchain4j.model.chat.response.PartialThinking p) {}
            @Override public void onPartialToolCall(dev.langchain4j.model.chat.response.PartialToolCall p) {}
            @Override public void onCompleteToolCall(dev.langchain4j.model.chat.response.CompleteToolCall c) {}
        });

        // 3. 主线程休眠，等待异步任务完成
        // 因为 chat() 是异步非阻塞的，主线程如果不睡，程序会直接退出，导致看不到输出
        Thread.sleep(60000);
    }

    @Test
    void m3(){
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
}

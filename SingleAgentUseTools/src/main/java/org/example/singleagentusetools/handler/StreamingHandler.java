package org.example.singleagentusetools.handler;

import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

/**
 * @author caiyuping
 * @date 2026/3/6 12:44
 * @description: 流式回调拦截器
 */
@Slf4j
public class StreamingHandler implements StreamingChatResponseHandler {

    // 当收到部分文本时的回调逻辑 (例如：发送给前端、打印到控制台)
    private final Consumer<String> onPartialCallback;

    // 当响应完成时的回调逻辑 (可选)
    private final Consumer<ChatResponse> onCompleteCallback;

    // 当发生错误时的回调逻辑 (可选，默认使用日志记录)
    private final Consumer<Throwable> onErrorCallback;

    /**
     * 构造函数：仅定义部分响应处理逻辑 (最常用)
     */
    public StreamingHandler(Consumer<String> onPartialCallback) {
        this(onPartialCallback, null, null);
    }

    /**
     * 构造函数：定义部分响应 + 完成响应 处理逻辑
     */
    public StreamingHandler(Consumer<String> onPartialCallback, Consumer<ChatResponse> onCompleteCallback) {
        this(onPartialCallback, onCompleteCallback, null);
    }

    /**
     * 全量构造函数
     */
    public StreamingHandler(Consumer<String> onPartialCallback,
                            Consumer<ChatResponse> onCompleteCallback,
                            Consumer<Throwable> onErrorCallback) {
        this.onPartialCallback = onPartialCallback;
        this.onCompleteCallback = onCompleteCallback;
        this.onErrorCallback = onErrorCallback != null ? onErrorCallback : this::defaultErrorHandler;
    }

    @Override
    public void onPartialResponse(String partialResponse) {
        if (partialResponse == null || partialResponse.isEmpty()) {
            return;
        }
        try {
            if (onPartialCallback != null) {
                onPartialCallback.accept(partialResponse);
            }
        } catch (Exception e) {
            log.error("处理部分响应时发生异常", e);
            // 如果业务逻辑崩溃，可以选择中断或记录，这里选择记录
        }
    }

    @Override
    public void onCompleteResponse(ChatResponse chatResponse) {
        try {
            if (onCompleteCallback != null) {
                onCompleteCallback.accept(chatResponse);
            } else {
                log.info("流式生成完成。Token 使用情况: {}",
                        chatResponse != null && chatResponse.metadata() != null ? chatResponse.metadata().tokenUsage() : "未知");
            }
        } catch (Exception e) {
            log.error("处理完成响应时发生异常", e);
        }
    }

    @Override
    public void onError(Throwable throwable) {
        try {
            onErrorCallback.accept(throwable);
        } catch (Exception e) {
            log.error("处理错误回调时发生二次异常", e);
        }
    }

    /**
     * 默认的错误处理：打印错误日志
     */
    private void defaultErrorHandler(Throwable throwable) {
        log.error("LLM 流式请求发生错误: {}", throwable.getMessage(), throwable);
    }
}

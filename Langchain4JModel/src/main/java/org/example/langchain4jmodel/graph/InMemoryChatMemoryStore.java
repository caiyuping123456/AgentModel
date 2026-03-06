package org.example.langchain4jmodel.graph;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageDeserializer;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
/**
 * @author caiyuping
 * @date 2026/3/6 16:37
 * @description: 业务
 */


/**
 * 简单的内存版 ChatMemoryStore 实现
 * ⚠️ 生产环境请替换为 RedisChatMemoryStore 或 JdbcChatMemoryStore
 */
@Component
public class InMemoryChatMemoryStore implements ChatMemoryStore {

    // Key: sessionId (Object), Value: JSON string of messages
    private final Map<Object, String> store = new ConcurrentHashMap<>();

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        String json = store.get(memoryId);
        if (json == null || json.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            // 使用 LangChain4j 官方反序列化工具
            return ChatMessageDeserializer.messagesFromJson(json);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize messages", e);
        }
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        try {
            // 使用 LangChain4j 官方序列化工具
            String json = ChatMessageSerializer.messagesToJson(messages);
            store.put(memoryId, json);
            System.out.println("💾 [ChatMemory] Updated " + messages.size() + " messages for session: " + memoryId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize messages", e);
        }
    }

    @Override
    public void deleteMessages(Object memoryId) {
        store.remove(memoryId);
        System.out.println("🗑️ [ChatMemory] Deleted messages for session: " + memoryId);
    }
}
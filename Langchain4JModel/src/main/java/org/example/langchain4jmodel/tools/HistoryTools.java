package org.example.langchain4jmodel.tools;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.agent.tool.ToolSpecifications;
import dev.langchain4j.service.tool.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.langchain4jmodel.pojo.ChatHistory;
import org.example.langchain4jmodel.service.ChatHistoryService;
import org.example.langchain4jmodel.utils.ToolProviderResultUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * @author caiyuping
 * @date 2026/3/4 16:17
 * @description: 用于聊天记录地保存
 */
@Component
@Slf4j
public class HistoryTools implements ToolProvider {

    @Resource
    private ChatHistoryService chatHistoryService;

    @Tool("将当前有意义的对话（用户提问 + AI回答）保存到数据库。" +
            "触发条件：1. 用户明确要求记录；2. 对话包含重要事实、数据、天气结论、生活建议或解决方案。" +
            "不要保存：简单的问候（如'你好'）、感谢（如'谢谢'）、无意义的闲聊。" +
            "参数 content 应包含：'用户问：[问题] \\n AI答：[核心回答摘要]'")
    public String saveImportantConversation(
            @P("用户的唯一标识符 (User ID)") Long userId,
            @P("需要保存的对话内容摘要，格式应为：'用户问：... \\n AI答：...'") String content,
            @P("对话发生的时间，通常为当前时间") LocalDateTime time
    ) {
        log.info(">>> [AI决策] 检测到重要对话，正在执行存储...");
        log.info("用户ID: {}", userId);
        log.info("存储内容: {}", content);
        try {
            ChatHistory record = ChatHistory.builder()
                    .UserId(userId)
                    .content(content)
                    .time(time)
                    .build();

            return chatHistoryService.analyzeAndSave(record);
        } catch (Exception e) {
            return "[AI决策] 存储失败";
        }
    }

    @Override
    public ToolProviderResult provideTools(ToolProviderRequest toolProviderRequest) {
        return ToolProviderResultUtils.extractToolsFromInstance(this);
    }
}

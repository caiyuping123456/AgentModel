package org.example.langchain4jmodel.pojo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author caiyuping
 * @date 2026/3/4 16:01
 * @description: 聊天记录
 */
@Data
@Builder
public class ChatHistory {
    private Long UserId;
    private String content;
    private LocalDateTime time;
}

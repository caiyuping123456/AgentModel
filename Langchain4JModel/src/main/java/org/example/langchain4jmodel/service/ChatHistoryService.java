package org.example.langchain4jmodel.service;

import jakarta.annotation.Resource;
import org.example.langchain4jmodel.mapper.ChatLLMMapper;
import org.example.langchain4jmodel.pojo.ChatHistory;
import org.springframework.stereotype.Service;

/**
 * @author caiyuping
 * @date 2026/3/4 16:23
 * @description: 业务
 */
@Service
public class ChatHistoryService {
    @Resource
    private ChatLLMMapper chatLLMMapper;

    public String analyzeAndSave(ChatHistory chatHistory){
        return chatLLMMapper.insert(chatHistory) == 1?"保存成功":"保存失败";
    }
}

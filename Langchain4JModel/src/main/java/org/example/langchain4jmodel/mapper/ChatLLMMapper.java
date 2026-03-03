package org.example.langchain4jmodel.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.langchain4jmodel.pojo.ChatHistory;

/**
 * @author caiyuping
 * @date 2026/3/4 15:58
 * @description: 用于Ai自行判断存储的Mapper
 */
@Mapper
public interface ChatLLMMapper {
    int insert(ChatHistory chatHistory);
}

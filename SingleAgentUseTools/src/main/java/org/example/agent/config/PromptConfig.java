package org.example.agent.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author caiyuping
 * @date 2026/3/10 10:09
 * @description: 系统提示词
 */
@Configuration
@Data
public class PromptConfig {
    @Value("${agent.system_prompt}")
    private String SYSTEM_PROMPT;
}

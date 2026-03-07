package org.example.singleagentusetools.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author caiyuping
 * @date 2026/3/9 14:05
 * @description: LLM配置类
 */
@ConfigurationProperties(prefix = "model")
@Configuration
@Data
public class NvidiaModelConfig {
    @Value("${model.llm.base-url}")
    private String BaseUrl;

    @Value("${model.llm.api-key}")
    private String ApiKey;

    @Value("${model.llm.model-name}")
    private String model;
}


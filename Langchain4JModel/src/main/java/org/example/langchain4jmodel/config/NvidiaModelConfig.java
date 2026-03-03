package org.example.langchain4jmodel.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @author caiyuping
 * @date 2026/3/3 16:41
 * @description: 大模型配置文件
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

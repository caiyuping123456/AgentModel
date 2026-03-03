package org.example.langchain4jmodel.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author caiyuping
 * @date 2026/3/3 19:32
 * @description: 业务
 */
@Component
@ConfigurationProperties
@Data
public class WeatherConfig {
    @Value("${weather.model}")
    private String model;

    @Value("${weather.weather-url}")
    private String WeatherUrl;
}

package org.example.langchain4jmodel.agent.weather;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.model.output.structured.Description;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * @author caiyuping
 * @date 2026/3/3 17:14
 * @description: 天气Agent
 */
public interface WeatherAgent {
    // 系统提示词：定义Agent的行为准则和能力边界
    @SystemMessage("agent.weather.system-message")

    // 用户交互方法：接收自然语言请求，Agent自主决策调用工具
    @UserMessage("{{request}}")
    @Agent
    String handleWeatherRequest(@V("request") String userRequest);
}

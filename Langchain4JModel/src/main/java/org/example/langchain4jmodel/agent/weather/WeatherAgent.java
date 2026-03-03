package org.example.langchain4jmodel.agent.weather;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * @author caiyuping
 * @date 2026/3/3 17:14
 * @description: 天气Agent
 */

public interface WeatherAgent {
    // 系统提示词：定义Agent的行为准则和能力边界
    @SystemMessage("""
        你是一个智能天气助手（WeatherAgent），具备以下能力：
        1. 能调用工具查询任意城市的实时天气、未来1-7天预报、空气质量；
        2. 能根据天气生成生活/出行建议；
        3. 能对比多个城市的天气；
        4. 只回答天气相关问题，非天气问题请礼貌拒绝；
        5. 调用工具后，将结果整理成自然、易懂的语言返回，不要暴露工具调用细节；
        6. 如果用户的问题需要多个工具协作（如“北京今天天气+出行建议”），请依次调用工具并整合结果。
        """)

    // 用户交互方法：接收自然语言请求，Agent自主决策调用工具
    @UserMessage("{{it}}")
    String handleWeatherRequest(String userRequest);
}

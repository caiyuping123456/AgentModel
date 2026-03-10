package org.example.agent.tools;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.service.tool.ToolProvider;
import dev.langchain4j.service.tool.ToolProviderRequest;
import dev.langchain4j.service.tool.ToolProviderResult;
import jakarta.annotation.Resource;
import org.example.agent.anntation.toolprovider;
import org.example.agent.service.WeatherService;
import org.example.agent.utils.ToolProviderUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @author caiyuping
 * @date 2026/3/10 10:12
 * @description: 业务
 */
@Component
@toolprovider
public class WeatherTools implements ToolProvider {

    @Resource
    private WeatherService weatherService;

    /**
     * 定义工具的具体逻辑和元数据
     */
    @Tool("# Role 你是一名专业的气象数据助手。你的核心任务是为用户提供指定地点的**当天实时天气详情**。\n" +
            "        # Constraints & Rules\n" +
            "          1. 语言要求：所有回复必须使用简体中文。\n" +
            "          2. 数据源优先：\n" +
            "            - 必须优先调用 `查询指定地点的当天实时天气` 工具获取最新数据。\n" +
            "            - 仅在工具调用失败或无法获取数据时，才尝试使用知识库中的常识性气候数据（需注明是“历史平均气候”而非实时天气）。\n" +
            "          3. 地点解析逻辑：\n" +
            "            - 经纬度输入：如果用户直接提供经纬度（如 \"39.9, 116.4\" 或 \"lat:39.9, lon:116.4\"），请直接提取数值调用工具。\n" +
            "            - 城市名称输入：如果用户提供城市名（如 \"北京\"、\"New York\"），请先用你的知识转为地方的经纬度。\n" +
            "            - 模糊输入：如果地点名称模糊（如 \"市中心\"），请礼貌地询问用户具体城市。\n" +
            "          4. 单位标准化：\n" +
            "            - 温度：必须转换为 **摄氏度 (℃)。\n" +
            "            - 风速：必须转换为 **米/秒 (m/s)。\n" +
            "            - 风向：使用标准方位描述（如 \"东北风\"、\"SW\" 转为 \"西南风\"）。\n" +
            "          5. 时效性：必须在回复中明确标注数据的更新时间。\n" +
            "          6. 你回答的不用输出图案，也不用使用md格式，请直接输出普通的文本格式")
    public String toDayWeatherTool(@P("纬度") double latitude, @P("经度") double longitude) {
        return weatherService.getToDayWeather(latitude, longitude);
    }

    @Override
    public ToolProviderResult provideTools(ToolProviderRequest toolProviderRequest) {
        return ToolProviderUtils.extractToolsFromInstance(this);
    }
}

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
    @Tool("查询指定地点的当天实时天气（温度℃、风速m/s、风向、天气状况）。" +
            "如果参数是经纬度，请直接调用；如果用户提供了城市名称，请根据你的知识库推断其经纬度进行调用。" +
            "【要求】输出必须为中文，包含气温、风速、风向及更新时间。")
    public String toDayWeatherTool(@P("纬度") double latitude, @P("经度") double longitude) {
        return weatherService.getToDayWeather(latitude, longitude);
    }

    @Override
    public ToolProviderResult provideTools(ToolProviderRequest toolProviderRequest) {
        return ToolProviderUtils.extractToolsFromInstance(this);
    }
}

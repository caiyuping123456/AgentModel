package org.example.langchain4jmodel.tools;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.service.tool.*;
import jakarta.annotation.Resource;
import org.example.langchain4jmodel.service.WeatherService;
import org.example.langchain4jmodel.utils.ToolProviderResultUtils;
import org.springframework.stereotype.Component;
/**
 * @author caiyuping
 * @date 2026/3/3 17:43
 * @description: 天气工具提供者，通过动态 ToolProvider 注册工具
 */
@Component
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

    /**
     * 实现 ToolProvider 接口，动态提供工具描述和执行逻辑
     */
    @Override
    public ToolProviderResult provideTools(ToolProviderRequest toolProviderRequest) {
        return ToolProviderResultUtils.extractToolsFromInstance(this);
    }
}
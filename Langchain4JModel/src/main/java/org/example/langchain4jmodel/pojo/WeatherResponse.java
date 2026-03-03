package org.example.langchain4jmodel.pojo;
import lombok.Data;
/**
 * @author caiyuping
 * @date 2026/3/3 19:55
 * @description: 业务根响应实体（匹配返回的 JSON 结构）
 */
@Data
public class WeatherResponse {
    // 地理信息
    private double latitude; // 纬度
    private double longitude; // 经度
    private double generationtime_ms; // 生成时间（毫秒）
    private int utc_offset_seconds; // UTC 偏移秒数
    private String timezone; // 时区
    private String timezone_abbreviation; // 时区缩写
    private double elevation; // 海拔

    // 单位说明
    private CurrentWeatherUnits current_weather_units;
    // 核心天气数据
    private CurrentWeather current_weather;
}

/**
 * 天气单位实体
 */
@Data
class CurrentWeatherUnits {
    private String time;
    private String interval;
    private String temperature; // 温度单位（°C）
    private String windspeed; // 风速单位（km/h）
    private String winddirection; // 风向单位（°）
    private String is_day;
    private String weathercode;
}

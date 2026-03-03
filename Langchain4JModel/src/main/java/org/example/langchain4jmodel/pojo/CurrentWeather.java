package org.example.langchain4jmodel.pojo;
import lombok.Data;

/**
 * @author caiyuping
 * @date 2026/3/3 19:58
 * @description: 实时天气核心数据
 */
@Data
public class CurrentWeather {
    private String time; // 数据时间（iso8601）
    private int interval; // 间隔（秒）
    private double temperature; // 温度（数值）
    private double windspeed; // 风速（数值）
    private int winddirection; // 风向（角度）
    private int is_day; // 是否白天（0=否，1=是）
    private int weathercode; // 天气码（WMO 编码，对应晴/多云等）
}
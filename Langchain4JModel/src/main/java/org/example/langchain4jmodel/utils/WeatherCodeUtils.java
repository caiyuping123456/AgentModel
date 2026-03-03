package org.example.langchain4jmodel.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author caiyuping
 * @date 2026/3/3 20:37
 * @description: 业务
 */
public class WeatherCodeUtils {

    // 1. 初始化天气码映射字典（不可变，线程安全）
    private static final Map<Integer, String> WEATHER_CODE_MAP;

    // 静态代码块初始化映射关系（类加载时执行，只初始化一次）
    static {
        Map<Integer, String> map = new HashMap<>();
        // 晴/云系
        map.put(0, "晴朗");
        map.put(1, "主要晴朗/少云");
        map.put(2, "部分多云");
        map.put(3, "阴天");
        // 雾
        map.put(45, "雾");
        map.put(48, "霜雾");
        // 毛毛雨
        map.put(51, "毛毛雨（轻）");
        map.put(53, "毛毛雨（中）");
        map.put(55, "毛毛雨（浓）");
        map.put(56, "冻毛毛雨（轻）");
        map.put(57, "冻毛毛雨（浓）");
        // 降雨
        map.put(61, "雨（轻）");
        map.put(63, "雨（中）");
        map.put(65, "雨（浓）");
        map.put(66, "冻雨（轻）");
        map.put(67, "冻雨（浓）");
        // 降雪
        map.put(71, "雪（轻）");
        map.put(73, "雪（中）");
        map.put(75, "雪（浓）");
        map.put(77, "雪粒");
        // 阵雨
        map.put(80, "阵雨（轻）");
        map.put(81, "阵雨（中）");
        map.put(82, "阵雨（猛）");
        // 阵雪
        map.put(85, "阵雪（轻）");
        map.put(86, "阵雪（猛）");
        // 雷阵雨
        map.put(95, "雷阵雨");
        map.put(96, "雷阵雨+小冰雹");
        map.put(99, "雷阵雨+大冰雹");

        // 封装为不可变 Map，防止后续被修改（线程安全）
        WEATHER_CODE_MAP = Collections.unmodifiableMap(map);
    }

    /**
     * 根据天气代码获取对应的天气描述（对应 Python 的 get_weather_desc 方法）
     *
     * @param weatherCode 天气码（整数类型）
     * @return 天气描述，未知代码返回 "未知天气（代码：XXX）"
     */
    public static String getWeatherDesc(int weatherCode) {
        // 从映射表获取，不存在则返回默认值
        return WEATHER_CODE_MAP.getOrDefault(weatherCode,
                String.format("未知天气（代码：%d）", weatherCode));
    }

    // 私有化构造方法，禁止实例化工具类
    private WeatherCodeUtils() {
    }
}

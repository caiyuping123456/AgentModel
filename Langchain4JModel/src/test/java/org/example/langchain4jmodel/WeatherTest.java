package org.example.langchain4jmodel;

import jakarta.annotation.Resource;
import org.example.langchain4jmodel.agent.weather.WeatherAgent;
import org.example.langchain4jmodel.service.WeatherService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author caiyuping
 * @date 2026/3/3 19:50
 * @description: 业务
 */
@SpringBootTest
public class WeatherTest {
    @Resource
    private WeatherService weatherService;

    @Resource
    private WeatherAgent weatherAgent;

    @Test
    public void m1(){
        System.out.println(weatherService.getToDayWeather(25.65, 114.77));
    }

    @Test
    public void m2(){
        String msg = weatherAgent.handleWeatherRequest("查询纬度 25.85，经度 114.93 的天气。,请说出你调用了什么tool" );
        System.out.println(msg);
    }
}

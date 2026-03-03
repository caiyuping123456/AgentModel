package org.example.langchain4jmodel.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.agent.tool.P;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.TimeValue;
import org.example.langchain4jmodel.config.WeatherConfig;
import org.example.langchain4jmodel.pojo.CurrentWeather;
import org.example.langchain4jmodel.pojo.WeatherResponse;
import org.example.langchain4jmodel.utils.WeatherCodeUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author caiyuping
 * @date 2026/3/3 19:15
 * @description: 连接天气接口客户端
 */
@Component
public class WeatherService {
    private RestTemplate restTemplate;
    final private ObjectMapper objectMapper = new ObjectMapper();
    @Resource
    private WeatherConfig weatherConfig;

    @PostConstruct
    public void init(){
        //初始化Http连接池
        PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager();
        poolingHttpClientConnectionManager.setMaxTotal(50);
        poolingHttpClientConnectionManager.setDefaultMaxPerRoute(20);

        //配置 HttpClient
        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(poolingHttpClientConnectionManager)
                .evictIdleConnections(TimeValue.of(Duration.ofMinutes(5)))
                .build();

        //配置请求工厂
        HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        httpComponentsClientHttpRequestFactory.setConnectTimeout(Duration.ofSeconds(3));

        this.restTemplate =  new RestTemplate(httpComponentsClientHttpRequestFactory);
    }

    public String getToDayWeather(double latitude,double longitude){
        try{
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            /**
             * "latitude": latitude,
             *         "longitude": longitude,
             *         "current_weather": True, ##获取当前的天气
             *         "models": "cma_grapes_global",
             */
            params.add("latitude",latitude+"");//纬度
            params.add("longitude",longitude+"");//经度
            params.add("current_weather","true");//表示查询今天的天气
            params.add("models","cma_grapes_global");//气象站
            String url = UriComponentsBuilder.fromHttpUrl(weatherConfig.getWeatherUrl())
                    .queryParams(params)
                    .toUriString();
            ResponseEntity<String> response  = restTemplate.getForEntity(url, String.class);


            // 解析 JSON 字符串为实体类
            WeatherResponse weatherResponse = objectMapper.readValue(response.getBody(), WeatherResponse.class);

            CurrentWeather currentWeather = weatherResponse.getCurrent_weather();
            double temperature = currentWeather.getTemperature(); // 温度：12.7
            double windspeed = currentWeather.getWindspeed(); // 风速：20.2
            int winddirection = currentWeather.getWinddirection(); // 风向：1
            int weathercode = currentWeather.getWeathercode(); // 天气码：2
            String weatherDesc = WeatherCodeUtils.getWeatherDesc(weathercode);
            String timezone = weatherResponse.getTimezone(); // 时区：GMT
            String weatherInfo = String.format(
                    "【天气信息】时区：%s，温度：%.1f °C，风速：%.1f km/h，风向：%d °，天气：%s（代码：%d）",
                    timezone, temperature, windspeed, winddirection, weatherDesc, weathercode
            );
            return weatherInfo;
        }catch (Exception e){
            return "出现未知情况，无法获取数据"+e;
        }
    }
}

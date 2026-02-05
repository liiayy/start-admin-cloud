package cn.muziseo.service.demo.module.external.service;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import cn.muziseo.service.demo.module.external.feign.WeatherFeignClient;
import cn.muziseo.service.demo.module.external.model.WeatherResponseDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 业务服务：演示两种调用方式
 */
@Slf4j
@Service
public class ExternalDemoService {

    @Value("${external.weather.key:xxxx}")
    private String apiKey;

    @Value("${external.weather.url:https://api.seniverse.com/v3}")
    private String weatherUrl;

    @Resource
    private WeatherFeignClient weatherFeignClient;

    /**
     * 方式一：使用 Feign Client (推荐)
     */
    public WeatherResponseDTO getWeatherByFeign(String city) {
        log.info("【Feign】正在查询城市天气: {}", city);
        try {
            return weatherFeignClient.getNowWeather(apiKey, city);
        } catch (Exception e) {
            log.error("【Feign】请求失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 方式二：使用 Hutool HttpUtil (轻量级)
     */
    public WeatherResponseDTO getWeatherByHutool(String city) {
        log.info("【Hutool】正在查询城市天气: {}", city);

        String fullUrl = weatherUrl + "/weather/now.json";

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("key", apiKey);
        paramMap.put("location", city);

        try {
            // 发送 GET 请求并设置超时
            String result = HttpUtil.get(fullUrl, paramMap, 5000);

            log.info("【Hutool】响应原始内容: {}", result);

            // 使用 Hutool JSON 工具反序列化
            return JSONUtil.toBean(result, WeatherResponseDTO.class);
        } catch (Exception e) {
            log.error("【Hutool】请求异常: {}", e.getMessage());
            return null;
        }
    }
}

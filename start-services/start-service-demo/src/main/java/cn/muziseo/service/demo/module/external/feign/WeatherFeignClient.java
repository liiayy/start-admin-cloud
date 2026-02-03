package cn.muziseo.service.demo.module.external.feign;

import cn.muziseo.service.demo.module.external.config.ExternalFeignConfig;
import cn.muziseo.service.demo.module.external.model.WeatherResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 声明式 Feign Client
 * url 从配置文件获取，默认为某公共 API
 */
@FeignClient(name = "external-weather-api", url = "${external.weather.url:https://api.seniverse.com/v3}", configuration = ExternalFeignConfig.class)
public interface WeatherFeignClient {

    @GetMapping("/weather/now.json")
    WeatherResponseDTO getNowWeather(
            @RequestParam("key") String apiKey,
            @RequestParam("location") String location);
}

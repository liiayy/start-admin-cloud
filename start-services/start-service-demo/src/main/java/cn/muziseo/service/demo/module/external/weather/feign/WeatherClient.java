package cn.muziseo.service.demo.module.external.weather.feign;

import cn.muziseo.service.demo.module.external.model.WeatherResponseDTO;
import cn.muziseo.service.demo.module.external.weather.config.WeatherFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 心知天气客户端
 */
@FeignClient(name = "weather-api-client", url = "${external.weather.url:https://api.seniverse.com/v3}", configuration = WeatherFeignConfig.class)
public interface WeatherClient {

    @GetMapping("/weather/now.json")
    WeatherResponseDTO getNow(@RequestParam("location") String location);
}

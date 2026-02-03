package cn.muziseo.service.demo.controller;

import cn.muziseo.common.core.domain.dto.ResponseDTO;
import cn.muziseo.service.demo.module.external.aliyun.feign.AliyunClient;
import cn.muziseo.service.demo.module.external.model.WeatherResponseDTO;
import cn.muziseo.service.demo.module.external.weather.feign.WeatherClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "多第三方系统测试")
@RestController
@RequestMapping("/external")
public class MultiSystemController {

    @Resource
    private WeatherClient weatherClient;

    @Resource
    private AliyunClient aliyunClient;

    @Operation(summary = "测试天气系统 (Query Params Auth)")
    @GetMapping("/weather")
    public ResponseDTO<WeatherResponseDTO> testWeather(@RequestParam String city) {
        // 由于有拦截器注入了 key，这里只需要传业务参数
        return ResponseDTO.success(weatherClient.getNow(city));
    }

    @Operation(summary = "测试阿里云系统 (Header Auth)")
    @PostMapping("/aliyun/sms")
    public ResponseDTO<String> testAliyun(@RequestBody Map<String, Object> params) {
        // 由于有拦截器注入了签名 Header，这里直接传业务 Body
        return ResponseDTO.success(aliyunClient.send(params));
    }
}

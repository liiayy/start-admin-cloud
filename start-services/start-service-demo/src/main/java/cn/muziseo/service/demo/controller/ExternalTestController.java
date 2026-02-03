package cn.muziseo.service.demo.controller;

import cn.muziseo.common.core.domain.dto.ResponseDTO;
import cn.muziseo.service.demo.module.external.model.WeatherResponseDTO;
import cn.muziseo.service.demo.module.external.service.ExternalDemoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "第三方接口演示")
@RestController
@RequestMapping("/external/test")
public class ExternalTestController {

    @Resource
    private ExternalDemoService externalDemoService;

    @Operation(summary = "通过 Feign 获取天气")
    @GetMapping("/weather/feign")
    public ResponseDTO<WeatherResponseDTO> testFeign(@RequestParam String city) {
        return ResponseDTO.success(externalDemoService.getWeatherByFeign(city));
    }

    @Operation(summary = "通过 Hutool 获取天气")
    @GetMapping("/weather/hutool")
    public ResponseDTO<WeatherResponseDTO> testHutool(@RequestParam String city) {
        return ResponseDTO.success(externalDemoService.getWeatherByHutool(city));
    }
}

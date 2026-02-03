package cn.muziseo.service.demo.module.external.model;

import lombok.Data;

/**
 * 示例：外部天气接口响应 DTO
 */
@Data
public class WeatherResponseDTO {
    private String status;
    private WeatherData result;

    @Data
    public static class WeatherData {
        private String location;
        private String temperature;
        private String text;
    }
}

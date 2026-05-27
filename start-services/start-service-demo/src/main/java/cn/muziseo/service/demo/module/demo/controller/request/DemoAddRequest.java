package cn.muziseo.service.demo.module.demo.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 演示产品新增/修改请求
 *
 * @author Antigravity
 */
@Data
@Schema(description = "演示数据新增/修改请求")
public class DemoAddRequest {

    @Schema(description = "名称")
    @NotBlank(message = "名称不能为空")
    private String name;

}

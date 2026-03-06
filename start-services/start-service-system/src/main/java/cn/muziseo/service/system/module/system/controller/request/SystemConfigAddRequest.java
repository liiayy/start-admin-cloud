package cn.muziseo.service.system.module.system.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 系统配置添加请求
 *
 * @author 木子软件
 * @Date 2026-02-27
 */
@Data
@Schema(description = "系统配置添加请求")
public class SystemConfigAddRequest {

    @Schema(description = "配置键", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "配置键不能为空")
    private String configKey;

    @Schema(description = "配置值", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "配置值不能为空")
    private String configValue;

    @Schema(description = "配置名称")
    private String configName;

    @Schema(description = "配置类型（string:字符串 number:数字 boolean:布尔）")
    private String configType;

    @Schema(description = "是否系统内置（0否 1是）")
    private Integer isSystem;

    @Schema(description = "备注")
    private String remark;
}

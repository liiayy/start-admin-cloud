package cn.muziseo.service.system.module.system.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 系统参数添加/更新请求
 *
 * @author 木子软件
 */
@Data
@Schema(description = "系统参数添加请求")
public class SystemConfigAddRequest {

    @Schema(description = "参数名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "参数名称不能为空")
    private String name;

    @Schema(description = "参数键名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "参数键名不能为空")
    private String configKey;

    @Schema(description = "参数键值", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "参数键值不能为空")
    private String configValue;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "是否系统内置（Y=是，N=否）")
    private String builtin;
}

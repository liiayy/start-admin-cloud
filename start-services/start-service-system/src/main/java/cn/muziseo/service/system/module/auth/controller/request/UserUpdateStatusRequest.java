package cn.muziseo.service.system.module.auth.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "用户状态更新请求")
public class UserUpdateStatusRequest {

    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "用户ID不能为空")
    private Long id;

    @Schema(description = "帐号状态（0正常 1停用）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "状态不能为空")
    private Integer status;
}

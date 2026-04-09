package cn.muziseo.service.system.module.auth.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "用户角色分配请求")
public class UserRoleAssignRequest {

    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @Schema(description = "角色ID列表")
    private List<Long> roleIds;
}

package cn.muziseo.service.system.module.permission.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 角色添加请求
 */
@Data
@Schema(description = "角色添加请求")
public class RoleAddRequest {

    @Schema(description = "角色名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "角色名称不能为空")
    private String name;

    @Schema(description = "角色权限字符串", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "角色权限字符串不能为空")
    private String code;

    @Schema(description = "显示顺序", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "显示顺序不能为空")
    private Integer sort;

    @Schema(description = "状态（0正常 1停用）")
    private Integer status;

    @Schema(description = "数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）")
    private Integer dataScope;

    @Schema(description = "数据范围(指定部门数组)")
    private String dataScopeDeptIds;

    @Schema(description = "角色类型")
    private Integer type;

    @Schema(description = "备注")
    private String remark;

}

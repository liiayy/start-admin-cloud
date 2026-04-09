package cn.muziseo.service.system.module.permission.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 角色修改请求
 *
 * @author 木子软件
 */
@Data
@Schema(description = "角色修改请求")
public class RoleUpdateRequest {

    @Schema(description = "角色ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "角色ID不能为空")
    private Long id;

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

    @Schema(description = "数据范围（1：全部 2：自定义 3：本部门 4：本部门及以下）")
    private Integer dataScope;

    @Schema(description = "数据范围部门ID数组")
    private String dataScopeDeptIds;

    @Schema(description = "角色类型")
    private Integer type;

    @Schema(description = "备注")
    private String remark;
}

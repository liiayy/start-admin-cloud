package cn.muziseo.service.system.module.organization.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 部门添加请求
 *
 * @author 木子软件
 * @Date 2026-02-11
 */
@Data
@Schema(description = "部门添加请求")
public class DeptAddRequest {

    @Schema(description = "部门名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "部门名称不能为空")
    private String name;

    @Schema(description = "父部门ID")
    private Long parentId;

    @Schema(description = "显示顺序", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "显示顺序不能为空")
    private Integer sort;

    @Schema(description = "负责人用户ID")
    private Long leaderUserId;

    @Schema(description = "联系电话")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "状态（0正常 1停用）")
    private Integer status;

    @Schema(description = "备注")
    private String remark;

}

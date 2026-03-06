package cn.muziseo.service.system.module.organization.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 岗位添加请求
 *
 * @author 木子软件
 * @Date 2026-02-11
 */
@Data
@Schema(description = "岗位添加请求")
public class PostAddRequest {

    @Schema(description = "岗位编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "岗位编码不能为空")
    private String code;

    @Schema(description = "岗位名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "岗位名称不能为空")
    private String name;

    @Schema(description = "显示顺序", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "显示顺序不能为空")
    private Integer sort;

    @Schema(description = "状态（0正常 1停用）")
    private Integer status;

    @Schema(description = "备注")
    private String remark;

}

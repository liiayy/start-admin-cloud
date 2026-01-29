package cn.muziseo.service.system.module.auth.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 菜单添加请求
 */
@Data
@Schema(description = "菜单添加请求")
public class MenuAddRequest {

    @Schema(description = "菜单名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "菜单名称不能为空")
    private String name;

    @Schema(description = "权限标识")
    private String permission;

    @Schema(description = "菜单类型（M目录 C菜单 F按钮）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "菜单类型不能为空")
    private String type;

    @Schema(description = "父菜单ID")
    private Long parentId;

    @Schema(description = "显示顺序")
    @NotNull(message = "显示顺序不能为空")
    private Integer sort;

    @Schema(description = "路由地址")
    private String path;

    @Schema(description = "组件路径")
    private String component;

    @Schema(description = "菜单图标")
    private String icon;

    @Schema(description = "状态（0正常 1停用）")
    private Integer status;

    @Schema(description = "是否可见")
    private Boolean visible;

    @Schema(description = "是否缓存")
    private Boolean keepAlive;

}

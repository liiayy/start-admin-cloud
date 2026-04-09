package cn.muziseo.service.system.module.permission.controller.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 菜单响应 VO
 *
 * @author 木子软件
 */
@Data
@Builder
@Schema(description = "菜单响应")
public class MenuVO {

    @Schema(description = "菜单ID")
    private Long id;

    @Schema(description = "菜单名称")
    private String name;

    @Schema(description = "权限标识")
    private String permission;

    @Schema(description = "菜单类型（1目录 2菜单 3按钮）")
    private Integer type;

    @Schema(description = "父菜单ID")
    private Long parentId;

    @Schema(description = "显示顺序")
    private Integer sort;

    @Schema(description = "路由地址")
    private String path;

    @Schema(description = "组件路径")
    private String component;

    @Schema(description = "组件名")
    private String componentName;

    @Schema(description = "菜单图标")
    private String icon;

    @Schema(description = "状态（0正常 1停用）")
    private Integer status;

    @Schema(description = "是否可见")
    private Boolean visible;

    @Schema(description = "是否缓存")
    private Boolean keepAlive;

    @Schema(description = "是否总是显示")
    private Boolean alwaysShow;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}

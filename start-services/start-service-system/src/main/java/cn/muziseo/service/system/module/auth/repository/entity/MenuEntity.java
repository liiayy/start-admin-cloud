package cn.muziseo.service.system.module.auth.repository.entity;

import cn.muziseo.common.db.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 菜单/权限表
 *
 * @Author Antigravity
 * @Date 2026-01-29
 */
@TableName("sys_menu")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuEntity extends BaseEntity {

    /**
     * 菜单ID
     */
    @TableId
    private Long id;

    /**
     * 菜单名称
     */
    private String name;

    /**
     * 权限标识
     */
    private String permission;

    /**
     * 菜单类型（M目录 C菜单 F按钮）
     */
    private String type;

    /**
     * 父菜单ID
     */
    private Long parentId;

    /**
     * 显示顺序
     */
    private Integer sort;

    /**
     * 路由地址
     */
    private String path;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 菜单状态（0正常 1停用）
     */
    private Integer status;

    /**
     * 是否可见（0显示 1隐藏）
     */
    private Boolean visible;

    /**
     * 是否缓存（0缓存 1不缓存）
     */
    private Boolean keepAlive;

}

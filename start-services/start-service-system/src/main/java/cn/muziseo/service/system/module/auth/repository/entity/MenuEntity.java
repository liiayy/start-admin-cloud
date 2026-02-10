package cn.muziseo.service.system.module.auth.repository.entity;

import cn.muziseo.common.db.entity.BaseEntity;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.*;

/**
 * 菜单/权限实体
 * <p>
 * 对应数据库表 sys_menu，用于存储系统的菜单结构、权限标识等信息
 *
 * @author 木子软件
 * @Date 2026-01-29
 * @Wechat liiayy
 * @Email 773582348@qq.com
 * @Copyright <a href="https://code.muziseo.cn">木子软件</a>
 */
@Table("sys_menu")
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
    @Id
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

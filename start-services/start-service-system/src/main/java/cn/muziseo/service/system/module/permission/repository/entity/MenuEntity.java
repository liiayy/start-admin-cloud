package cn.muziseo.service.system.module.permission.repository.entity;

import cn.muziseo.common.core.constant.DictConstants;
import cn.muziseo.common.core.datatracer.annotation.DataTracerFieldDict;
import cn.muziseo.common.core.datatracer.annotation.DataTracerFieldIgnore;
import cn.muziseo.common.core.datatracer.annotation.DataTracerFieldLabel;
import cn.muziseo.common.db.entity.BaseEntity;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.*;

/**
 * 菜单/权限实体
 * <p>
 * 对应数据库表 system_menu，用于存储系统的菜单结构、权限标识等信息
 *
 * @author 木子软件
 * @Date 2026-01-29
 * @Wechat liiayy
 * @Email 773582348@qq.com
 * @Copyright <a href="https://code.muziseo.cn">木子软件</a>
 */
@Table("system_menu")
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
    @DataTracerFieldLabel("菜单名称")
    private String name;

    /**
     * 权限标识
     */
    @DataTracerFieldLabel("权限标识")
    private String permission;

    /**
     * 菜单类型（1：目录 2：菜单 3：按钮）
     */
    @DataTracerFieldLabel("菜单类型")
    private Integer type;

    /**
     * 父菜单ID
     */
    @DataTracerFieldLabel("父菜单ID")
    private Long parentId;

    /**
     * 显示顺序
     */
    @DataTracerFieldIgnore
    private Integer sort;

    /**
     * 路由地址
     */
    @DataTracerFieldLabel("路由地址")
    private String path;

    /**
     * 组件路径
     */
    @DataTracerFieldLabel("组件路径")
    private String component;

    /**
     * 组件名
     */
    @DataTracerFieldIgnore
    private String componentName;

    /**
     * 菜单图标
     */
    @DataTracerFieldLabel("菜单图标")
    private String icon;

    /**
     * 菜单状态（0正常 1停用）
     */
    @DataTracerFieldLabel("菜单状态")
    @DataTracerFieldDict(dictType = DictConstants.SYS_STATUS)
    private Integer status;

    /**
     * 是否可见（0显示 1隐藏）
     */
    @DataTracerFieldLabel("显示状态")
    private Boolean visible;

    /**
     * 是否缓存（0缓存 1不缓存）
     */
    @DataTracerFieldLabel("缓存状态")
    private Boolean keepAlive;

    /**
     * 是否总是显示
     */
    @DataTracerFieldIgnore
    private Boolean alwaysShow;

}

package cn.muziseo.service.system.module.permission.repository.entity;

import cn.muziseo.common.db.entity.BaseEntity;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.*;
import cn.muziseo.common.core.datatracer.annotation.DataTracerFieldLabel;
import cn.muziseo.common.core.datatracer.annotation.DataTracerFieldDict;

/**
 * 角色实体
 * <p>
 * 对应数据库表 system_role，用于存储系统的角色信息，支持角色权限管理和数据权限控制
 *
 * @author 木子软件
 * @Date 2026-01-29
 * @Wechat liiayy
 * @Email 773582348@qq.com
 * @Copyright <a href="https://code.muziseo.cn">木子软件</a>
 */
@Table("system_role")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleEntity extends BaseEntity {

    /**
     * 角色ID
     */
    @Id
    private Long id;

    /**
     * 角色名称
     */
    @DataTracerFieldLabel("角色名称")
    private String name;

    /**
     * 角色权限字符串
     */
    @DataTracerFieldLabel("权限代码")
    private String code;

    /**
     * 显示顺序
     */
    private Integer sort;

    /**
     * 数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）
     */
    @DataTracerFieldLabel("数据范围")
    @DataTracerFieldDict(dictType = "sys_data_scope")
    private Integer dataScope;

    /**
     * 数据范围(指定部门数组)，JSON字符串存储部门ID列表
     */
    private String dataScopeDeptIds;

    /**
     * 角色状态（0正常 1停用）
     */
    @DataTracerFieldLabel("角色状态")
    @DataTracerFieldDict(dictType = "sys_common_status")
    private Integer status;

    /**
     * 角色类型
     */
    private Integer type;

    /**
     * 备注
     */
    private String remark;

}

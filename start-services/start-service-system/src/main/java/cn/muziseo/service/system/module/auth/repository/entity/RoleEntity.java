package cn.muziseo.service.system.module.auth.repository.entity;

import cn.muziseo.common.db.entity.BaseEntity;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.*;

/**
 * 角色实体
 * <p>
 * 对应数据库表 sys_role，用于存储系统的角色信息，支持角色权限管理
 *
 * @author 木子软件
 * @Date 2026-01-29
 * @Wechat liiayy
 * @Email 773582348@qq.com
 * @Copyright <a href="https://code.muziseo.cn">木子软件</a>
 */
@Table("sys_role")
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
    private String name;

    /**
     * 角色权限字符串
     */
    private String code;

    /**
     * 显示顺序
     */
    private Integer sort;

    /**
     * 角色状态（0正常 1停用）
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

}

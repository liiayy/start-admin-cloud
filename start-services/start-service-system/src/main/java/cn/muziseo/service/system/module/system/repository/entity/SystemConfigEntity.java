package cn.muziseo.service.system.module.system.repository.entity;

import cn.muziseo.common.db.entity.BaseEntity;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.*;

/**
 * 系统参数实体
 * <p>
 * 对应数据库表 system_config
 *
 * @author 木子软件
 * @Date 2026-02-27
 */
@Table("system_config")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemConfigEntity extends BaseEntity {

    /**
     * 参数主键
     */
    @Id
    private Long id;

    /**
     * 参数名称
     */
    private String name;

    /**
     * 参数键名
     */
    private String configKey;

    /**
     * 参数键值
     */
    private String configValue;

    /**
     * 是否系统内置（Y=是，N=否）
     */
    private String builtin;

    /**
     * 是否公开（Y=是，N=否）
     */
    private String isPublic;

    /**
     * 备注
     */
    private String remark;

}

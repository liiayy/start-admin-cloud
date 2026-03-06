package cn.muziseo.service.system.module.system.repository.entity;

import cn.muziseo.common.db.entity.BaseEntity;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.*;

/**
 * 系统配置实体
 * <p>
 * 对应数据库表 system_config，用于存储系统配置参数信息
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
     * 配置ID
     */
    @Id
    private Long id;

    /**
     * 配置键
     */
    private String configKey;

    /**
     * 配置值
     */
    private String configValue;

    /**
     * 配置名称
     */
    private String configName;

    /**
     * 配置类型（string:字符串 number:数字 boolean:布尔）
     */
    private String configType;

    /**
     * 是否系统内置（0否 1是）
     */
    private Integer isSystem;

    /**
     * 备注
     */
    private String remark;

}

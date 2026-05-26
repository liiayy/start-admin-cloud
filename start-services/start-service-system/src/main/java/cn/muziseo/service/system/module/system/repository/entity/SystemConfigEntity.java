package cn.muziseo.service.system.module.system.repository.entity;

import cn.muziseo.common.db.entity.BaseEntity;
import cn.muziseo.common.core.constant.DictConstants;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.*;
import cn.muziseo.common.core.datatracer.annotation.DataTracerFieldLabel;
import cn.muziseo.common.core.datatracer.annotation.DataTracerFieldDict;

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
    @DataTracerFieldLabel("参数名称")
    private String name;

    /**
     * 参数键名
     */
    @DataTracerFieldLabel("参数键名")
    private String configKey;

    /**
     * 参数键值
     */
    @DataTracerFieldLabel("参数键值")
    private String configValue;

    /**
     * 是否系统内置（Y=是，N=否）
     */
    private String builtin;

    /**
     * 是否公开（Y=是，N=否）
     */
    @DataTracerFieldLabel("是否公开")
    @DataTracerFieldDict(dictType = DictConstants.SYS_YES_NO)
    private String isPublic;

    /**
     * 备注
     */
    private String remark;

}

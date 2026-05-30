package cn.muziseo.service.system.module.system.repository.entity;

import cn.muziseo.common.core.constant.DictConstants;
import cn.muziseo.common.core.datatracer.annotation.DataTracerFieldDict;
import cn.muziseo.common.core.datatracer.annotation.DataTracerFieldIgnore;
import cn.muziseo.common.core.datatracer.annotation.DataTracerFieldLabel;
import cn.muziseo.common.db.entity.BaseEntity;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.*;

/**
 * 字典类型实体
 * <p>
 * 对应数据库表 system_dict_type，用于存储字典类型信息
 *
 * @author 木子软件
 * @Date 2026-02-27
 */
@Table("system_dict_type")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DictTypeEntity extends BaseEntity {

    /**
     * 字典类型ID
     */
    @Id(keyType = KeyType.Generator, value = "snowFlakeId")
    private Long id;

    /**
     * 字典类型
     */
    @DataTracerFieldLabel("字典类型")
    private String type;

    /**
     * 字典类型名称
     */
    @DataTracerFieldLabel("字典名称")
    private String name;

    /**
     * 状态（0正常 1停用）
     */
    @DataTracerFieldLabel("状态")
    @DataTracerFieldDict(dictType = DictConstants.SYS_STATUS)
    private Integer status;

    /**
     * 备注
     */
    @DataTracerFieldLabel("备注")
    private String remark;

}

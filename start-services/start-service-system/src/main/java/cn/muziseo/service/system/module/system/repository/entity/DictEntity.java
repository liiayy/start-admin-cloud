package cn.muziseo.service.system.module.system.repository.entity;

import cn.muziseo.common.core.constant.DictConstants;
import cn.muziseo.common.core.datatracer.annotation.DataTracerFieldDict;
import cn.muziseo.common.core.datatracer.annotation.DataTracerFieldIgnore;
import cn.muziseo.common.core.datatracer.annotation.DataTracerFieldLabel;
import cn.muziseo.common.db.entity.BaseEntity;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.*;

/**
 * 字典数据实体
 * <p>
 * 对应数据库表 system_dict_data，用于存储字典数据信息
 *
 * @author 木子软件
 * @Date 2026-02-27
 */
@Table("system_dict_data")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DictEntity extends BaseEntity {

    /**
     * 字典ID
     */
    @Id
    private Long id;

    /**
     * 字典类型
     */
    @DataTracerFieldLabel("字典类型")
    private String dictType;

    /**
     * 字典标签
     */
    @DataTracerFieldLabel("字典标签")
    private String label;

    /**
     * 字典值
     */
    @DataTracerFieldLabel("字典键值")
    private String value;

    /**
     * 显示顺序
     */
    @DataTracerFieldIgnore
    private Integer sort;

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

    /**
     * 颜色类型
     */
    @DataTracerFieldLabel("标签颜色")
    private String colorType;

    /**
     * CSS 样式
     */
    @DataTracerFieldIgnore
    private String cssClass;

}

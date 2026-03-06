package cn.muziseo.service.system.module.system.repository.entity;

import cn.muziseo.common.db.entity.BaseEntity;
import com.mybatisflex.annotation.Id;
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
    @Id
    private Long id;

    /**
     * 字典类型编码
     */
    private String code;

    /**
     * 字典类型名称
     */
    private String name;

    /**
     * 显示顺序
     */
    private Integer sort;

    /**
     * 状态（0正常 1停用）
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

}

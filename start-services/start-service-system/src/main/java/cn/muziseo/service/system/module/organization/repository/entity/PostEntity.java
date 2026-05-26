package cn.muziseo.service.system.module.organization.repository.entity;

import cn.muziseo.common.db.annotation.DataColumn;
import cn.muziseo.common.db.entity.BaseEntity;
import cn.muziseo.common.core.constant.DictConstants;
import cn.muziseo.common.core.datatracer.annotation.DataTracerFieldDict;
import cn.muziseo.common.core.datatracer.annotation.DataTracerFieldLabel;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.*;

/**
 * 岗位实体
 * <p>
 * 对应数据库表 system_post，用于存储岗位信息
 *
 * @author 木子软件
 * @Date 2026-01-29
 * @Wechat liiayy
 * @Email 773582348@qq.com
 * @Copyright <a href="https://code.muziseo.cn">木子软件</a>
 */
@Table("system_post")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostEntity extends BaseEntity {

    /**
     * 岗位ID
     */
    @Id
    private Long id;

    /**
     * 所属部门ID
     */
    @DataColumn(DataColumn.DataType.DEPT)
    @DataTracerFieldLabel("所属部门ID")
    private Long deptId;

    /**
     * 岗位编码
     */
    @DataTracerFieldLabel("岗位编码")
    private String code;

    /**
     * 岗位名称
     */
    @DataTracerFieldLabel("岗位名称")
    private String name;

    /**
     * 显示顺序
     */
    @DataTracerFieldLabel("显示顺序")
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

}

package cn.muziseo.service.system.module.organization.repository.entity;

import cn.muziseo.common.db.entity.BaseEntity;
import cn.muziseo.common.core.constant.DictConstants;
import cn.muziseo.common.core.datatracer.annotation.DataTracerFieldDict;
import cn.muziseo.common.core.datatracer.annotation.DataTracerFieldLabel;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.*;

/**
 * 部门实体
 * <p>
 * 对应数据库表 system_dept，用于存储组织架构的部门信息，支持树形结构
 *
 * @author 木子软件
 * @Date 2026-01-29
 * @Wechat liiayy
 * @Email 773582348@qq.com
 * @Copyright <a href="https://code.muziseo.cn">木子软件</a>
 */
@Table("system_dept")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeptEntity extends BaseEntity {

    /**
     * 部门ID
     */
    @Id(keyType = KeyType.Generator, value = "snowFlakeId")
    private Long id;

    /**
     * 部门名称
     */
    @DataTracerFieldLabel("部门名称")
    private String name;

    /**
     * 父部门ID
     */
    @DataTracerFieldLabel("父部门ID")
    private Long parentId;

    /**
     * 显示顺序
     */
    @DataTracerFieldLabel("显示顺序")
    private Integer sort;

    /**
     * 负责人用户ID
     */
    @DataTracerFieldLabel("负责人ID")
    private Long leaderUserId;

    /**
     * 联系电话
     */
    @DataTracerFieldLabel("联系电话")
    private String phone;

    /**
     * 邮箱
     */
    @DataTracerFieldLabel("邮箱")
    private String email;

    /**
     * 部门状态（0正常 1停用）
     */
    @DataTracerFieldLabel("部门状态")
    @DataTracerFieldDict(dictType = DictConstants.SYS_STATUS)
    private Integer status;

    /**
     * 备注
     */
    @DataTracerFieldLabel("备注")
    private String remark;

}

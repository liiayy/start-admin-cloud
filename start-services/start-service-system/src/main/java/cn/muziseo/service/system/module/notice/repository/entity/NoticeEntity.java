package cn.muziseo.service.system.module.notice.repository.entity;

import cn.muziseo.common.db.entity.BaseEntity;
import cn.muziseo.common.core.constant.DictConstants;
import cn.muziseo.common.core.datatracer.annotation.DataTracerFieldDict;
import cn.muziseo.common.core.datatracer.annotation.DataTracerFieldLabel;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.*;

/**
 * 通知公告实体类
 * 
 * @author 木子软件
 */
@Table("system_notice")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeEntity extends BaseEntity {

    /**
     * 公告ID
     */
    @Id(keyType = KeyType.Generator, value = "snowFlakeId")
    private Long id;

    /**
     * 公告标题
     */
    @DataTracerFieldLabel("公告标题")
    private String title;

    /**
     * 公告类型 (1通知 2公告)
     */
    @DataTracerFieldLabel("公告类型")
    @DataTracerFieldDict(dictType = DictConstants.SYS_NOTICE_TYPE)
    private Integer type;

    /**
     * 公告内容
     */
    @DataTracerFieldLabel("公告内容")
    private String content;

    /**
     * 公告状态 (0正常 1关闭)
     */
    @DataTracerFieldLabel("公告状态")
    @DataTracerFieldDict(dictType = DictConstants.SYS_NOTICE_STATUS)
    private Integer status;

    /**
     * 发布范围 (0全部 1指定)
     */
    @DataTracerFieldLabel("发布范围")
    @DataTracerFieldDict(dictType = DictConstants.SYS_NOTICE_TARGET_TYPE)
    private Integer targetType;

    /**
     * 指定的部门ID (逗号分隔)
     */
    @DataTracerFieldLabel("指定部门")
    private String targetDepts;

    /**
     * 指定的角色ID (逗号分隔)
     */
    @DataTracerFieldLabel("指定角色")
    private String targetRoles;

    /**
     * 指定的岗位ID (逗号分隔)
     */
    @DataTracerFieldLabel("指定岗位")
    private String targetPosts;
}

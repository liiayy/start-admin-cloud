package cn.muziseo.service.system.module.notice.repository.entity;

import cn.muziseo.common.db.entity.BaseEntity;
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
    private String title;

    /**
     * 公告类型 (1通知 2公告)
     */
    private Integer type;

    /**
     * 公告内容
     */
    private String content;

    /**
     * 公告状态 (0正常 1关闭)
     */
    private Integer status;

    /**
     * 发布范围 (0全部 1指定)
     */
    private Integer targetType;

    /**
     * 指定的部门ID (逗号分隔)
     */
    private String targetDepts;

    /**
     * 指定的角色ID (逗号分隔)
     */
    private String targetRoles;

    /**
     * 指定的岗位ID (逗号分隔)
     */
    private String targetPosts;
}

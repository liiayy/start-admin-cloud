package cn.muziseo.service.system.module.notice.controller.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 通知公告信息 VO
 * 
 * @author 木子软件
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "通知公告信息")
public class NoticeVO {

    @Schema(description = "公告ID")
    private Long id;

    @Schema(description = "公告标题")
    private String title;

    @Schema(description = "公告类型 (1通知 2公告)")
    private Integer type;

    @Schema(description = "公告内容")
    private String content;

    @Schema(description = "状态 (0正常 1关闭)")
    private Integer status;

    @Schema(description = "创建人")
    private String creator;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "是否已读 (状态补偿)")
    private Boolean isRead;

    @Schema(description = "发布范围 (0全部 1指定)")
    private Integer targetType;

    @Schema(description = "指定的部门ID (逗号分隔)")
    private String targetDepts;

    @Schema(description = "指定的角色ID (逗号分隔)")
    private String targetRoles;

    @Schema(description = "指定的岗位ID (逗号分隔)")
    private String targetPosts;
}

package cn.muziseo.service.system.module.notice.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 通知公告更新请求
 * 
 * @author 木子软件
 */
@Data
@Schema(description = "通知公告更新请求")
public class NoticeUpdateRequest {

    /**
     * 公告ID
     */
    @Schema(description = "公告ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "公告ID不能为空")
    private Long id;

    /**
     * 公告标题
     */
    @Schema(description = "公告标题", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "公告标题不能为空")
    private String title;

    /**
     * 公告类型 (1通知 2公告)
     */
    @Schema(description = "公告类型 (1通知 2公告)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "公告类型不能为空")
    private Integer type;

    /**
     * 公告内容
     */
    @Schema(description = "公告内容", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "公告内容不能为空")
    private String content;

    /**
     * 状态 (0正常 1关闭)
     */
    @Schema(description = "状态 (0正常 1关闭)")
    private Integer status;

    /**
     * 发布范围 (0全部 1指定)
     */
    @Schema(description = "发布范围 (0全部 1指定)")
    private Integer targetType;

    /**
     * 指定的部门ID (逗号分隔)
     */
    @Schema(description = "指定的部门ID")
    private String targetDepts;

    /**
     * 指定的角色ID (逗号分隔)
     */
    @Schema(description = "指定的角色ID")
    private String targetRoles;

    /**
     * 指定的岗位ID (逗号分隔)
     */
    @Schema(description = "指定的岗位ID")
    private String targetPosts;
}

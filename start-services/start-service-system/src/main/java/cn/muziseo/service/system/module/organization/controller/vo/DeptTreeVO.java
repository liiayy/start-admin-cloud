package cn.muziseo.service.system.module.organization.controller.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 部门树节点 VO
 *
 * @author 木子软件
 */
@Data
@Builder
@Schema(description = "部门树节点")
public class DeptTreeVO {

    @Schema(description = "部门ID")
    private Long id;

    @Schema(description = "部门名称")
    private String name;

    @Schema(description = "父部门ID")
    private Long parentId;

    @Schema(description = "显示顺序")
    private Integer sort;

    @Schema(description = "负责人用户ID")
    private Long leaderUserId;

    @Schema(description = "联系电话")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "状态（0正常 1停用）")
    private Integer status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "子部门列表")
    private List<DeptTreeVO> children;
}

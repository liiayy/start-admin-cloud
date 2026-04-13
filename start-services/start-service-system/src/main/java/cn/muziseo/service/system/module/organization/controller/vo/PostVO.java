package cn.muziseo.service.system.module.organization.controller.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 岗位信息 VO
 *
 * @author 木子软件
 */
@Data
@Builder
@Schema(description = "岗位信息")
public class PostVO {

    @Schema(description = "岗位ID")
    private Long id;

    @Schema(description = "岗位编码")
    private String code;

    @Schema(description = "岗位名称")
    private String name;

    @Schema(description = "显示顺序")
    private Integer sort;

    @Schema(description = "状态（0正常 1停用）")
    private Integer status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}

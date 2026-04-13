package cn.muziseo.service.system.module.organization.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 岗位分页查询请求
 *
 * @author 木子软件
 */
@Data
@Schema(description = "岗位分页查询请求")
public class PostPageRequest {

    @Schema(description = "所属部门ID（包含子部门）")
    private Long deptId;

    @Schema(description = "岗位名称（模糊匹配）")
    private String name;

    @Schema(description = "状态（0正常 1停用）")
    private Integer status;

    @Schema(description = "页码", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页条数", example = "10")
    private Integer pageSize = 10;

}

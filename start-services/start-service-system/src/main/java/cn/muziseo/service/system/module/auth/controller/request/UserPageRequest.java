package cn.muziseo.service.system.module.auth.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户分页查询请求")
public class UserPageRequest {

    @Schema(description = "用户账号（模糊匹配）")
    private String username;

    @Schema(description = "手机号码（模糊匹配）")
    private String mobile;

    @Schema(description = "帐号状态（0正常 1停用）")
    private Integer status;

    @Schema(description = "部门ID")
    private Long deptId;

    @Schema(description = "页码", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页条数", example = "10")
    private Integer pageSize = 10;
}

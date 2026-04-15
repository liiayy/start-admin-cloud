package cn.muziseo.service.system.module.system.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 系统参数分页查询请求
 *
 * @author 木子软件
 */
@Data
@Schema(description = "系统参数分页查询请求")
public class SystemConfigPageRequest {

    @Schema(description = "参数名称（模糊匹配）")
    private String name;

    @Schema(description = "参数键名（模糊匹配）")
    private String configKey;

    @Schema(description = "页码", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页条数", example = "10")
    private Integer pageSize = 10;
}

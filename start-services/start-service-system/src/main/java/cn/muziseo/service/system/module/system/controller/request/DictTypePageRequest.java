package cn.muziseo.service.system.module.system.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 字典类型分页查询请求
 *
 * @author 木子软件
 */
@Data
@Schema(description = "字典类型分页查询请求")
public class DictTypePageRequest {

    @Schema(description = "字典名称（模糊匹配）")
    private String name;

    @Schema(description = "字典类型（模糊匹配）")
    private String type;

    @Schema(description = "状态（0正常 1停用）")
    private Integer status;

    @Schema(description = "页码", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页条数", example = "10")
    private Integer pageSize = 10;
}

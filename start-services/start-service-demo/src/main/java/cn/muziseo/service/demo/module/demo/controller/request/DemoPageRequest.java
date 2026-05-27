package cn.muziseo.service.demo.module.demo.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 演示产品分页查询请求
 *
 * @author 木子软件
 */
@Data
@Schema(description = "演示数据分页查询请求")
public class DemoPageRequest {

    @Schema(description = "名称（模糊匹配）")
    private String name;

    @Schema(description = "页码", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页条数", example = "10")
    private Integer pageSize = 10;

}

package cn.muziseo.service.system.module.notice.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 通知公告分页查询请求
 * 
 * @author 木子软件
 */
@Data
@Schema(description = "通知公告分页查询请求")
public class NoticePageRequest {

    @Schema(description = "公告标题（模糊匹配）")
    private String title;

    @Schema(description = "公告类型 (1通知 2公告)")
    private Integer type;

    @Schema(description = "状态 (0正常 1关闭)")
    private Integer status;

    @Schema(description = "页码", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页条数", example = "10")
    private Integer pageSize = 10;
}

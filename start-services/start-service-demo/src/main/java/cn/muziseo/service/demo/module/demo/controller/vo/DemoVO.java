package cn.muziseo.service.demo.module.demo.controller.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 演示产品视图对象
 *
 * @author 木子软件
 */
@Data
@Schema(description = "演示数据视图对象")
public class DemoVO {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "创建者")
    private String creator;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新者")
    private String updater;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}

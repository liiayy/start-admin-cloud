package cn.muziseo.service.system.module.monitor.datatracer.controller.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "数据变更记录视图对象")
public class DataTracerVO {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "业务ID")
    private Long dataId;

    @Schema(description = "业务类型")
    private Integer type;

    @Schema(description = "操作内容")
    private String content;

    @Schema(description = "变更前数据")
    private String diffOld;

    @Schema(description = "变更后数据")
    private String diffNew;

    @Schema(description = "操作人账号")
    private String operName;

    @Schema(description = "操作IP")
    private String operIp;

    @Schema(description = "操作地点")
    private String operLocation;

    @Schema(description = "UserAgent")
    private String userAgent;

    @Schema(description = "操作时间")
    private LocalDateTime createTime;
}

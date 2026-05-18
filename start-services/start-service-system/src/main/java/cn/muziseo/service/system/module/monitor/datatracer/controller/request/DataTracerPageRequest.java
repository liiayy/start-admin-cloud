package cn.muziseo.service.system.module.monitor.datatracer.controller.request;

import cn.muziseo.common.db.page.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "数据变更记录分页请求")
public class DataTracerPageRequest extends PageRequest {

    @Schema(description = "业务ID")
    private Long dataId;

    @Schema(description = "业务类型")
    private Integer type;

    @Schema(description = "操作人账号")
    private String operName;

    @Schema(description = "操作时间(开始)")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime beginTime;

    @Schema(description = "操作时间(结束)")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
}

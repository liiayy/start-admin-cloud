package cn.muziseo.service.system.module.system.controller.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * OSS 文件元数据视图对象
 *
 * @author 木子软件
 */
@Data
@Builder
@Schema(description = "OSS 文件元数据视图")
public class SysOssVO implements Serializable {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "文件名")
    private String fileName;

    @Schema(description = "原始文件名")
    private String originalName;

    @Schema(description = "文件后缀")
    private String fileSuffix;

    @Schema(description = "文件地址")
    private String url;

    @Schema(description = "文件大小 (Byte)")
    private Long size;

    @Schema(description = "内容类型")
    private String contentType;

    @Schema(description = "存储平台")
    private String service;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}

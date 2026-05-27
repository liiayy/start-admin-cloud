package cn.muziseo.service.system.module.system.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 远程调用文件信息 DTO
 *
 * @author 木子软件
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileRemoteDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 资源ID
     */
    private Long id;

    /**
     * 文件URL路径
     */
    private String url;

    /**
     * 存储的文件名
     */
    private String fileName;

    /**
     * 原文件名
     */
    private String originalName;

    /**
     * 文件后缀名
     */
    private String fileSuffix;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;
}

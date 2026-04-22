package cn.muziseo.common.oss.entity;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * OSS 上传结果包装类
 *
 * @author 木子软件
 */
@Data
@Builder
public class UploadResult implements Serializable {

    /**
     * 文件访问地址 (完整 URL)
     */
    private String url;

    /**
     * 对象键 (存储中的唯一文件名)
     */
    private String fileName;

    /**
     * 存储类型标识 (如: aliyun, minio, local)
     */
    private String service;

}

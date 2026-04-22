package cn.muziseo.common.oss.entity;

import cn.muziseo.common.oss.enums.AccessPolicyType;
import lombok.Data;

import java.io.Serializable;

/**
 * OSS 客户端配置实体
 *
 * @author 木子软件
 */
@Data
public class OssProperties implements Serializable {

    /**
     * 配置名称/标识 (如 aliyun-1)
     */
    private String configKey;

    /**
     * 存储平台 (如 local, aliyun, minio)
     */
    private String service;

    /**
     * 访问密钥 (S3 AK)
     */
    private String accessKey;

    /**
     * 秘密密钥 (S3 SK)
     */
    private String secretKey;

    /**
     * 存储桶名称
     */
    private String bucketName;

    /**
     * 路径前缀
     */
    private String prefix;

    /**
     * 服务端点 (OSS/MinIO 为必填)
     */
    private String endpoint;

    /**
     * 自定义域名
     */
    private String domain;

    /**
     * 区域标识
     */
    private String region;

    /**
     * 是否使用 HTTPS
     */
    private Boolean isHttps;

    /**
     * 桶权限类型
     */
    private AccessPolicyType accessPolicy;

}

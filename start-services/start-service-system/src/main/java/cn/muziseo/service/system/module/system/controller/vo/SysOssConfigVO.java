package cn.muziseo.service.system.module.system.controller.vo;

import cn.muziseo.common.oss.enums.AccessPolicyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * OSS 存储配置 VO
 *
 * @author Antigravity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SysOssConfigVO {
    private Long id;
    private String configKey;
    private String service;
    private String accessKey;
    private String secretKey;
    private String bucketName;
    private String prefix;
    private String endpoint;
    private String domain;
    private String region;
    private Boolean isHttps;
    private AccessPolicyType accessPolicy;
    private Integer status;
    private String remark;
    private LocalDateTime createTime;
}

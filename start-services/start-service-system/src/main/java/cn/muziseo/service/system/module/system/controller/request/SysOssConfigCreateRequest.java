package cn.muziseo.service.system.module.system.controller.request;

import cn.muziseo.common.oss.enums.AccessPolicyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * OSS 存储配置保存/修改 Request
 *
 * @author Antigravity
 */
@Data
public class SysOssConfigCreateRequest {
    
    private Long id;

    @NotBlank(message = "配置标识不能为空")
    private String configKey;

    @NotBlank(message = "存储平台不能为空")
    private String service;

    private String accessKey;
    private String secretKey;
    private String bucketName;
    private String prefix;
    private String endpoint;
    private String domain;
    private String region;
    private Boolean isHttps;
    
    @NotNull(message = "桶权限类型不能为空")
    private AccessPolicyType accessPolicy;
    
    private Integer status;
    private String remark;
}

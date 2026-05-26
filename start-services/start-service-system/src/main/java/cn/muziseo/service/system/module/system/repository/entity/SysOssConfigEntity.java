package cn.muziseo.service.system.module.system.repository.entity;

import cn.muziseo.common.db.entity.BaseEntity;
import cn.muziseo.common.core.constant.DictConstants;
import cn.muziseo.common.core.datatracer.annotation.DataTracerFieldDict;
import cn.muziseo.common.core.datatracer.annotation.DataTracerFieldLabel;
import cn.muziseo.common.oss.enums.AccessPolicyType;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * OSS 存储配置实体
 *
 * @author 木子软件
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("system_oss_config")
public class SysOssConfigEntity extends BaseEntity {

    /**
     * 主键ID
     */
    @Id(keyType = KeyType.Generator, value = "snowFlakeId")
    private Long id;

    /**
     * 配置标识 (唯一且不能重复)
     */
    @DataTracerFieldLabel("配置标识")
    private String configKey;

    /**
     * 存储平台 (如 local, aliyun, minio)
     */
    @DataTracerFieldLabel("存储平台")
    private String service;

    /**
     * Access Key (AK)
     */
    @DataTracerFieldLabel("Access Key")
    private String accessKey;

    /**
     * Secret Key (SK)
     */
    @DataTracerFieldLabel("Secret Key")
    private String secretKey;

    /**
     * 存储桶名称
     */
    @DataTracerFieldLabel("存储桶名称")
    private String bucketName;

    /**
     * 存储前缀
     */
    @DataTracerFieldLabel("存储前缀")
    private String prefix;

    /**
     * 服务端点 (OSS/MinIO 为必填)
     */
    @DataTracerFieldLabel("服务端点")
    private String endpoint;

    /**
     * 展示域名/自定义域名
     */
    @DataTracerFieldLabel("展示域名")
    private String domain;

    /**
     * 区域标识
     */
    @DataTracerFieldLabel("区域标识")
    private String region;

    /**
     * 是否使用 HTTPS (0=否 1=是)
     */
    @DataTracerFieldLabel("是否使用 HTTPS")
    private Boolean isHttps;

    /**
     * 桶权限类型 (0私有 1公开读 2公开读写)
     */
    @DataTracerFieldLabel("桶权限类型")
    private AccessPolicyType accessPolicy;

    /**
     * 配置状态 (0正常 1停用)
     */
    @DataTracerFieldLabel("配置状态")
    @DataTracerFieldDict(dictType = DictConstants.SYS_STATUS)
    private Integer status;

    /**
     * 备注
     */
    @DataTracerFieldLabel("备注")
    private String remark;

}

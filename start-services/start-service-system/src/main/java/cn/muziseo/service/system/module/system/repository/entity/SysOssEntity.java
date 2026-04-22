package cn.muziseo.service.system.module.system.repository.entity;

import cn.muziseo.common.db.entity.BaseEntity;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * OSS 文件元数据实体
 *
 * @author 木子软件
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("system_oss")
public class SysOssEntity extends BaseEntity {

    /**
     * 主键ID
     */
    @Id(keyType = KeyType.Generator, value = "snowFlakeId")
    private Long id;

    /**
     * 对象存储中的文件名 (Object Key)
     */
    private String fileName;

    /**
     * 上传时的原始文件名
     */
    private String originalName;

    /**
     * 文件后缀名 (如 jpg)
     */
    private String fileSuffix;

    /**
     * 资源访问 URL
     */
    private String url;

    /**
     * 文件大小 (Byte)
     */
    private Long size;

    /**
     * 存储配置键 (oss_config_key)
     */
    private String service;

    /**
     * 内容类型 (MIME)
     */
    private String contentType;

    /**
     * 文件 MD5 校验值
     */
    private String md5;

}

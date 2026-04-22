package cn.muziseo.common.oss.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * OSS 业务配置属性
 *
 * @author 木子软件
 */
@Data
@ConfigurationProperties(prefix = "start.oss")
public class OssConfigProperties {

    /**
     * 最大上传大小 (字节)，默认 20MB
     */
    private long maxSize = 20 * 1024 * 1024;

    /**
     * 禁止上传的后缀列表
     */
    private List<String> forbiddenSuffix = new ArrayList<>();

}

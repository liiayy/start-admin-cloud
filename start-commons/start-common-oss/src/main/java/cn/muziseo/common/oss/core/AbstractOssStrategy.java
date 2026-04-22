package cn.muziseo.common.oss.core;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.muziseo.common.oss.entity.OssProperties;
import cn.muziseo.common.oss.entity.UploadResult;
import lombok.Getter;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * OSS 客户端基类
 *
 * @author 木子软件
 */
public abstract class AbstractOssStrategy implements OssClient {

    @Getter
    protected OssProperties properties;

    @Override
    public String getConfigKey() {
        return properties.getConfigKey();
    }

    /**
     * 生成存储路径
     * 格式: {prefix}/{moduleName}/{yyyy/MM/dd}/{uuid}.{suffix}
     *
     * @param suffix     后缀
     * @param moduleName 模块名
     * @return 完整路径
     */
    protected String getPath(String suffix, String moduleName) {
        StringBuilder path = new StringBuilder();
        // 前缀：确保不产生双斜杠
        if (StringUtils.hasText(properties.getPrefix())) {
            String prefix = properties.getPrefix();
            path.append(prefix);
            if (!prefix.endsWith("/")) {
                path.append("/");
            }
        }
        // 模块名
        if (StringUtils.hasText(moduleName)) {
            path.append(moduleName);
            if (!moduleName.endsWith("/")) {
                path.append("/");
            }
        }
        // 日期路径
        path.append(DateUtil.format(new Date(), "yyyy/MM/dd")).append("/");
        // UUID文件名
        path.append(IdUtil.fastSimpleUUID());
        // 后缀
        if (StringUtils.hasText(suffix)) {
            path.append(suffix.startsWith(".") ? suffix : "." + suffix);
        }
        return path.toString();
    }

    @Override
    public UploadResult uploadSuffix(byte[] data, String suffix, String moduleName) {
        return uploadSuffix(data, suffix, moduleName, null);
    }

    @Override
    public UploadResult uploadSuffix(byte[] data, String suffix, String moduleName, String contentType) {
        String key = getPath(suffix, moduleName);
        return upload(data, key, contentType);
    }
}

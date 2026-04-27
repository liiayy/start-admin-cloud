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
        // 模块名：强制路径清洗，防止路径穿越
        if (StringUtils.hasText(moduleName)) {
            String cleanModuleName = cleanPath(moduleName);
            if (StringUtils.hasText(cleanModuleName)) {
                path.append(cleanModuleName);
                if (!cleanModuleName.endsWith("/")) {
                    path.append("/");
                }
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
 
    /**
     * 路径清洗：防止路径穿越 (Path Traversal)
     * 过滤掉 .. 和 多余的斜杠
     */
    private String cleanPath(String path) {
        if (!StringUtils.hasText(path)) {
            return "";
        }
        // 1. 替换反斜杠
        String cleanPath = path.replace("\\", "/");
        // 2. 移除所有的 .. 字符
        cleanPath = cleanPath.replace("../", "").replace("./", "");
        // 3. 移除开头和结尾的斜杠
        while (cleanPath.startsWith("/")) {
            cleanPath = cleanPath.substring(1);
        }
        while (cleanPath.endsWith("/")) {
            cleanPath = cleanPath.substring(0, cleanPath.length() - 1);
        }
        return cleanPath;
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

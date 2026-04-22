package cn.muziseo.common.oss.core;

import cn.hutool.core.io.FileUtil;
import cn.muziseo.common.oss.entity.OssProperties;
import cn.muziseo.common.oss.entity.UploadResult;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.InputStream;

/**
 * 本地文件存储策略
 *
 * @author 木子软件
 */
@Slf4j
public class LocalOssStrategy extends AbstractOssStrategy {

    public LocalOssStrategy(OssProperties properties) {
        this.properties = properties;
    }

    @Override
    public UploadResult upload(byte[] data, String key, String contentType) {
        try {
            String path = getLocalPath(key);
            FileUtil.writeBytes(data, path);
            return UploadResult.builder()
                    .url(getUrl(key))
                    .fileName(key)
                    .service(properties.getConfigKey())
                    .build();
        } catch (Exception e) {
            log.error("本地文件上传失败: {}", e.getMessage());
            throw new RuntimeException("本地文件存储异常", e);
        }
    }

    @Override
    public UploadResult upload(InputStream inputStream, String key, String contentType) {
        try {
            String path = getLocalPath(key);
            FileUtil.writeFromStream(inputStream, path);
            return UploadResult.builder()
                    .url(getUrl(key))
                    .fileName(key)
                    .service(properties.getConfigKey())
                    .build();
        } catch (Exception e) {
            log.error("本地文件上传失败: {}", e.getMessage());
            throw new RuntimeException("本地文件存储异常", e);
        }
    }

    @Override
    public void delete(String key) {
        try {
            String path = getLocalPath(key);
            FileUtil.del(path);
        } catch (Exception e) {
            log.error("本地文件删除失败: {}", e.getMessage());
        }
    }

    @Override
    public String getUrl(String key) {
        // 本地存储走网关代理：/api/system/resource/oss/download/**
        return "/api/system/resource/oss/download/" + key;
    }

    /**
     * 获取本地绝对路径
     */
    private String getLocalPath(String key) {
        // endpoint 作为本地存储的根目录
        String baseDir = properties.getEndpoint();
        if (baseDir.endsWith("/") || baseDir.endsWith("\\")) {
            return baseDir + key;
        }
        return baseDir + File.separator + key;
    }
}

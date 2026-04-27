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
        // 本地存储走网关代理：/api/admin/system/oss/download/**
        String url = "/api/admin/system/oss/download/" + key;
        
        // 如果是私有读，增加临时访问令牌（类似于 S3 Presigned URL）
        if (cn.muziseo.common.oss.enums.AccessPolicyType.PRIVATE.equals(properties.getAccessPolicy())) {
            long expires = System.currentTimeMillis() / 1000 + 3600; // 默认 1 小时有效
            String secret = cn.hutool.core.util.StrUtil.blankToDefault(properties.getSecretKey(), "start-admin-default-secret");
            String token = cn.hutool.crypto.SecureUtil.hmacMd5(secret).digestHex(key + expires);
            url += "?expires=" + expires + "&token=" + token;
        }
        
        return url;
    }

    /**
     * 获取本地绝对路径（加固版：增加边界校验）
     */
    private String getLocalPath(String key) {
        // endpoint 作为本地存储的根目录
        String baseDir = properties.getEndpoint();
        File baseDirFile = new File(baseDir);
        String absoluteBaseDir = baseDirFile.getAbsolutePath();
        
        File file = new File(baseDirFile, key);
        String absoluteFilePath = file.getAbsolutePath();
        
        // 【核心加固】检查最终生成的路径是否还在基准目录下，防止通过 ../ 穿越
        if (!absoluteFilePath.startsWith(absoluteBaseDir)) {
            log.error("[安全拦截] 检测到非法的路径访问尝试: key={}", key);
            throw new RuntimeException("非法的存储路径");
        }
        
        return absoluteFilePath;
    }
}

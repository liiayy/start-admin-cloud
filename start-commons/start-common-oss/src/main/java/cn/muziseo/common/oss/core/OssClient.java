package cn.muziseo.common.oss.core;

import cn.muziseo.common.oss.entity.UploadResult;

import java.io.InputStream;

/**
 * OSS 客户端抽象接口
 *
 * @author 木子软件
 */
public interface OssClient {

    /**
     * 上传字节数组
     *
     * @param data 文件字节数据
     * @param key  对象存储键 (包含完整路径)
     * @param contentType MIME类型
     * @return 上传结果
     */
    UploadResult upload(byte[] data, String key, String contentType);

    /**
     * 上传文件流
     *
     * @param inputStream 输入流
     * @param key         对象存储键 (包含完整路径)
     * @param contentType MIME类型
     * @return 上传结果
     */
    UploadResult upload(InputStream inputStream, String key, String contentType);

    /**
     * 删除文件
     *
     * @param key 对象存储键
     */
    void delete(String key);

    /**
     * 获取资源 URL
     *
     * @param key 对象存储键
     * @return 资源访问地址
     */
    String getUrl(String key);

    /**
     * 根据后缀生成上传结果
     *
     * @param data       数据
     * @param suffix     后缀 (如 .jpg)
     * @param moduleName 模块名 (用于路径分类)
     * @param contentType MIME类型
     * @return 上传结果
     */
    UploadResult uploadSuffix(byte[] data, String suffix, String moduleName, String contentType);

    /**
     * 根据后缀生成上传结果 (不带 contentType)
     *
     * @param data       数据
     * @param suffix     后缀 (如 .jpg)
     * @param moduleName 模块名 (用于路径分类)
     * @return 上传结果
     */
    UploadResult uploadSuffix(byte[] data, String suffix, String moduleName);

    /**
     * 获取当前配置标识
     *
     * @return 配置 Key
     */
    String getConfigKey();
}

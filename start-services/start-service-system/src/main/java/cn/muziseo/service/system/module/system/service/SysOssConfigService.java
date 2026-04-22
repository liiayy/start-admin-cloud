package cn.muziseo.service.system.module.system.service;

import cn.muziseo.service.system.module.system.repository.entity.SysOssConfigEntity;

import java.util.List;

/**
 * OSS 存储配置业务接口
 *
 * @author 木子软件
 */
public interface SysOssConfigService {

    /**
     * 初始化 OSS 客户端工厂
     */
    void initOssFactory();

    /**
     * 更新并重新加载指定配置的客户端
     *
     * @param configKey 配置Key
     */
    void reloadClient(String configKey);

    /**
     * 新增或修改配置
     *
     * @param entity 配置实体
     */
    void saveConfig(SysOssConfigEntity entity);

    /**
     * 删除配置
     *
     * @param id 配置ID
     */
    void deleteConfig(Long id);

    /**
     * 根据配置Key获取配置
     */
    SysOssConfigEntity getByConfigKey(String configKey);

    /**
     * 获取所有配置
     */
    List<SysOssConfigEntity> listAll();
}

package cn.muziseo.service.system.module.system.service;

import cn.muziseo.service.system.module.system.controller.request.SystemConfigAddRequest;
import cn.muziseo.service.system.module.system.repository.entity.SystemConfigEntity;

import java.util.List;

/**
 * 系统配置业务接口
 *
 * @author 木子软件
 * @Date 2026-02-27
 */
public interface SystemConfigService {

    /**
     * 获取所有系统配置列表
     */
    List<SystemConfigEntity> list();

    /**
     * 根据ID获取系统配置
     */
    SystemConfigEntity getById(Long id);

    /**
     * 根据配置键获取配置值
     */
    String getConfigValue(String configKey);

    /**
     * 添加系统配置
     */
    void addConfig(SystemConfigAddRequest request);

    /**
     * 更新系统配置
     */
    void updateConfig(Long id, SystemConfigAddRequest request);

    /**
     * 删除系统配置
     */
    void deleteConfig(Long id);
}

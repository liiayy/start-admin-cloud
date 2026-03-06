package cn.muziseo.service.system.module.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.muziseo.service.system.module.system.controller.request.SystemConfigAddRequest;
import cn.muziseo.service.system.module.system.manager.SystemConfigManager;
import cn.muziseo.service.system.module.system.repository.entity.SystemConfigEntity;
import cn.muziseo.service.system.module.system.service.SystemConfigService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 系统配置业务实现
 *
 * @author 木子软件
 * @Date 2026-02-27
 */
@Service
@Slf4j
public class SystemConfigServiceImpl implements SystemConfigService {

    @Resource
    private SystemConfigManager systemConfigManager;

    @Override
    public List<SystemConfigEntity> list() {
        return systemConfigManager.list();
    }

    @Override
    public SystemConfigEntity getById(Long id) {
        return systemConfigManager.getById(id);
    }

    @Override
    public String getConfigValue(String configKey) {
        SystemConfigEntity config = systemConfigManager.getByConfigKey(configKey);
        return config != null ? config.getConfigValue() : null;
    }

    @Override
    public void addConfig(SystemConfigAddRequest request) {
        log.info("新增系统配置: configKey={}", request.getConfigKey());

        // 检查配置键是否存在
        if (systemConfigManager.existsByConfigKey(request.getConfigKey())) {
            throw new RuntimeException("配置键已存在");
        }

        SystemConfigEntity entity = BeanUtil.copyProperties(request, SystemConfigEntity.class);
        if (entity.getIsSystem() == null) {
            entity.setIsSystem(0);
        }
        systemConfigManager.save(entity);
        log.info("新增系统配置成功: configKey={}", entity.getConfigKey());
    }

    @Override
    public void updateConfig(Long id, SystemConfigAddRequest request) {
        log.info("更新系统配置: id={}", id);

        SystemConfigEntity existing = systemConfigManager.getById(id);
        if (existing == null) {
            throw new RuntimeException("系统配置不存在");
        }

        // 系统内置配置不允许修改配置键
        if (existing.getIsSystem() != null && existing.getIsSystem() == 1) {
            if (!existing.getConfigKey().equals(request.getConfigKey())) {
                throw new RuntimeException("系统内置配置不允许修改配置键");
            }
        }

        SystemConfigEntity entity = BeanUtil.copyProperties(request, SystemConfigEntity.class);
        entity.setId(id);
        entity.setIsSystem(existing.getIsSystem());
        systemConfigManager.updateById(entity);
        log.info("更新系统配置成功: id={}", id);
    }

    @Override
    public void deleteConfig(Long id) {
        log.info("删除系统配置: id={}", id);

        SystemConfigEntity existing = systemConfigManager.getById(id);
        if (existing == null) {
            throw new RuntimeException("系统配置不存在");
        }

        // 系统内置配置不允许删除
        if (existing.getIsSystem() != null && existing.getIsSystem() == 1) {
            throw new RuntimeException("系统内置配置不允许删除");
        }

        systemConfigManager.removeById(id);
        log.info("删除系统配置成功: id={}", id);
    }
}

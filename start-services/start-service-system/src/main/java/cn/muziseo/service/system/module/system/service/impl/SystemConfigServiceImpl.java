package cn.muziseo.service.system.module.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.muziseo.common.cache.config.ConfigCacheManager;
import cn.muziseo.common.core.exception.BusinessException;
import cn.muziseo.common.db.page.PageResponse;
import cn.muziseo.service.system.enums.SystemErrorCode;
import cn.muziseo.service.system.module.system.controller.request.SystemConfigAddRequest;
import cn.muziseo.service.system.module.system.controller.request.SystemConfigPageRequest;
import cn.muziseo.service.system.module.system.controller.vo.SystemConfigVO;
import cn.muziseo.service.system.module.system.manager.SystemConfigManager;
import cn.muziseo.service.system.module.system.repository.entity.SystemConfigEntity;
import cn.muziseo.service.system.module.system.service.SystemConfigService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 系统参数业务实现
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
    public PageResponse<SystemConfigVO> pageConfig(SystemConfigPageRequest request) {
        var page = systemConfigManager.pageConfig(request);
        List<SystemConfigVO> voList = page.getRecords().stream()
                .map(this::toConfigVO)
                .toList();
        return new PageResponse<>(voList, (int) page.getTotalRow());
    }

    @Override
    public SystemConfigVO getConfigById(Long id) {
        return toConfigVO(systemConfigManager.getById(id));
    }

    @Override
    public String getConfigValue(String configKey) {
        SystemConfigEntity config = systemConfigManager.getByConfigKey(configKey);
        // 只有标记为公开的参数才允许外部获取
        if (config != null && "Y".equals(config.getIsPublic())) {
            return config.getConfigValue();
        }
        return null;
    }

    @Override
    public java.util.Map<String, String> getConfigValues(java.util.List<String> configKeys) {
        if (cn.hutool.core.collection.CollectionUtil.isEmpty(configKeys)) {
            return java.util.Collections.emptyMap();
        }
        // 这里为了简单且保证安全过滤，直接循环调用 getConfigValue
        // 注意：后续可以优化为 SQL IN 查询以提升极端性能，目前基于 getByConfigKey (带索引) 性能已足够
        return configKeys.stream()
                .distinct()
                .map(key -> new java.util.AbstractMap.SimpleEntry<>(key, getConfigValue(key)))
                .filter(e -> e.getValue() != null)
                .collect(java.util.stream.Collectors.toMap(java.util.Map.Entry::getKey, java.util.Map.Entry::getValue));
    }

    @Override
    public void addConfig(SystemConfigAddRequest request) {
        // 检查参数键名是否已存在
        if (systemConfigManager.existsByConfigKey(request.getConfigKey())) {
            throw new BusinessException(SystemErrorCode.CONFIG_KEY_EXISTS);
        }

        SystemConfigEntity entity = BeanUtil.copyProperties(request, SystemConfigEntity.class);
        if (entity.getBuiltin() == null) {
            entity.setBuiltin("N");
        }
        if (entity.getIsPublic() == null) {
            entity.setIsPublic("N");
        }
        systemConfigManager.save(entity);
        // 清除缓存（新增也要清，因为可能之前查过返回了空值标记）
        ConfigCacheManager.evictCache(request.getConfigKey());
    }

    @Override
    public void updateConfig(Long id, SystemConfigAddRequest request) {
        SystemConfigEntity existing = systemConfigManager.getById(id);
        if (existing == null) {
            throw new BusinessException(SystemErrorCode.CONFIG_NOT_EXISTS);
        }

        // 系统内置参数不允许修改键名
        if ("Y".equals(existing.getBuiltin())
                && !existing.getConfigKey().equals(request.getConfigKey())) {
            throw new BusinessException(SystemErrorCode.CONFIG_BUILTIN_CANNOT_MODIFY);
        }

        // 如果键名变更，检查新键名是否已存在
        if (!existing.getConfigKey().equals(request.getConfigKey())
                && systemConfigManager.existsByConfigKey(request.getConfigKey())) {
            throw new BusinessException(SystemErrorCode.CONFIG_KEY_EXISTS);
        }

        SystemConfigEntity entity = BeanUtil.copyProperties(request, SystemConfigEntity.class);
        entity.setId(id);
        // 保留原有的 builtin 标记，不允许通过编辑修改
        entity.setBuiltin(existing.getBuiltin());
        systemConfigManager.updateById(entity);
        // 清除缓存（如果键名发生变更，需要清除旧键名和新键名两条缓存）
        ConfigCacheManager.evictCache(existing.getConfigKey());
        if (!existing.getConfigKey().equals(request.getConfigKey())) {
            ConfigCacheManager.evictCache(request.getConfigKey());
        }
    }

    @Override
    public void deleteConfig(Long id) {
        SystemConfigEntity existing = systemConfigManager.getById(id);
        if (existing == null) {
            throw new BusinessException(SystemErrorCode.CONFIG_NOT_EXISTS);
        }

        // 系统内置参数不允许删除
        if ("Y".equals(existing.getBuiltin())) {
            throw new BusinessException(SystemErrorCode.CONFIG_BUILTIN_CANNOT_DELETE);
        }

        systemConfigManager.removeById(id);
        // 清除缓存
        ConfigCacheManager.evictCache(existing.getConfigKey());
    }

    /**
     * Entity 转 VO
     */
    private SystemConfigVO toConfigVO(SystemConfigEntity entity) {
        if (entity == null) {
            return null;
        }
        return SystemConfigVO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .configKey(entity.getConfigKey())
                .configValue(entity.getConfigValue())
                .builtin(entity.getBuiltin())
                .isPublic(entity.getIsPublic())
                .remark(entity.getRemark())
                .createTime(entity.getCreateTime())
                .build();
    }
}

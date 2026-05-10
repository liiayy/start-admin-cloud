package cn.muziseo.service.system.module.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.muziseo.common.cache.config.ConfigCacheManager;
import cn.muziseo.common.core.exception.BusinessException;
import cn.muziseo.common.db.page.PageResponse;
import cn.muziseo.service.system.enums.SystemErrorCode;
import cn.muziseo.service.system.module.system.controller.request.SystemConfigCreateRequest;
import cn.muziseo.service.system.module.system.controller.request.SystemConfigPageRequest;
import cn.muziseo.service.system.module.system.controller.vo.SystemConfigVO;
import cn.muziseo.service.system.module.system.convert.SystemConfigConverter;
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

    @Resource
    private SystemConfigConverter systemConfigConverter;

    /**
     * 分页查询系统参数列表
     *
     * @param request 分页查询请求参数
     * @return 分页结果
     */
    @Override
    public PageResponse<SystemConfigVO> pageConfig(SystemConfigPageRequest request) {
        var page = systemConfigManager.pageConfig(request);
        List<SystemConfigVO> voList = page.getRecords().stream()
                .map(systemConfigConverter::toVO)
                .toList();
        return new PageResponse<>(voList, (int) page.getTotalRow());
    }

    /**
     * 根据 ID 获取参数配置详情
     *
     * @param id 参数 ID
     * @return 参数配置视图对象
     */
    @Override
    public SystemConfigVO getConfigById(Long id) {
        return systemConfigConverter.toVO(systemConfigManager.getById(id));
    }

    /**
     * 根据键名获取公开的参数值
     * <p>
     * 仅允许外部获取标记为公开 (isPublic='Y') 的参数
     *
     * @param configKey 参数键名
     * @return 参数值
     */
    @Override
    public String getConfigValue(String configKey) {
        SystemConfigEntity config = systemConfigManager.getByConfigKey(configKey);
        // 只有标记为公开的参数才允许外部获取
        if (config != null && "Y".equals(config.getIsPublic())) {
            return config.getConfigValue();
        }
        return null;
    }

    /**
     * 批量获取公开的参数值
     *
     * @param configKeys 参数键名列表
     * @return 键值对 Map
     */
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

    /**
     * 新增系统参数
     * <p>
     * 1. 检查参数键名是否已存在
     * 2. 保存并清除对应缓存
     *
     * @param request 新增参数请求
     */
    @Override
    public void createConfig(SystemConfigCreateRequest request) {
        // 检查参数键名是否已存在
        if (systemConfigManager.existsByConfigKey(request.getConfigKey())) {
            throw new BusinessException(SystemErrorCode.CONFIG_KEY_EXISTS);
        }

        SystemConfigEntity entity = systemConfigConverter.toEntity(request);
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

    /**
     * 修改系统参数
     * <p>
     * 1. 校验参数是否存在
     * 2. 系统内置参数不允许修改键名
     * 3. 若键名变更，检查新键名是否冲突
     * 4. 更新并清除相关缓存
     *
     * @param id      参数 ID
     * @param request 修改参数请求
     */
    @Override
    public void updateConfig(Long id, SystemConfigCreateRequest request) {
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

        SystemConfigEntity entity = systemConfigConverter.toEntity(request);
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

    /**
     * 删除系统参数
     * <p>
     * 1. 校验参数是否存在
     * 2. 系统内置参数不允许删除
     *
     * @param id 参数 ID
     */
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
}

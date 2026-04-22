package cn.muziseo.service.system.module.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.muziseo.common.core.exception.BusinessException;
import cn.muziseo.common.oss.entity.OssProperties;
import cn.muziseo.common.oss.factory.OssFactory;
import cn.muziseo.service.system.enums.OssErrorCode;
import cn.muziseo.service.system.module.system.manager.SysOssConfigManager;
import cn.muziseo.service.system.module.system.repository.entity.SysOssConfigEntity;
import cn.muziseo.service.system.module.system.service.SysOssConfigService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.update.UpdateChain;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * OSS 存储配置业务实现
 *
 * @author 木子软件
 */
@Service
@Slf4j
public class SysOssConfigServiceImpl implements SysOssConfigService {

    @Resource
    private SysOssConfigManager sysOssConfigManager;

    /**
     * 项目启动时自动初始化
     */
    @PostConstruct
    @Override
    public void initOssFactory() {
        log.info("开始初始化 OSS 客户端工厂...");
        List<SysOssConfigEntity> list = sysOssConfigManager.listEnabledConfig();
        for (SysOssConfigEntity config : list) {
            String configKey = config.getConfigKey();
            log.info("检查并初始化 OSS 配置: {}", configKey);
            OssFactory.init(toProperties(config));
        }
        log.info("OSS 客户端工厂初始化完成，共加载 {} 个配置", list.size());
    }

    @Override
    public void reloadClient(String configKey) {
        SysOssConfigEntity config = sysOssConfigManager.getByConfigKey(configKey);
        if (config == null || config.getStatus() != 0) {
            OssFactory.remove(configKey);
        } else {
            OssFactory.init(toProperties(config));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveConfig(SysOssConfigEntity entity) {
        // 1. 检查 Key 是否重复
        SysOssConfigEntity existing = sysOssConfigManager.getByConfigKey(entity.getConfigKey());
        if (existing != null && !existing.getId().equals(entity.getId())) {
            throw new BusinessException(OssErrorCode.OSS_CONFIG_KEY_EXISTS);
        }

        // 2. 新增时默认为停用状态
        if (entity.getId() == null) {
            entity.setStatus(1);
        }

        // 3. 状态切换逻辑
        if (entity.getStatus() == 0) {
            // 如果要启用当前配置，则将所有其他配置设为停用 (保持排他性)
            UpdateChain.of(SysOssConfigEntity.class)
                    .set(SysOssConfigEntity::getStatus, 1)
                    .where(SysOssConfigEntity::getStatus).eq(0)
                    .and(SysOssConfigEntity::getId).ne(entity.getId())
                    .update();
        } else {
            // 如果尝试停用当前配置，需检查是否是最后一个启用的
            if (entity.getId() != null) {
                SysOssConfigEntity old = sysOssConfigManager.getById(entity.getId());
                // 如果原本是启用的，现在要改为停用
                if (old != null && old.getStatus() == 0) {
                    long activeCount = sysOssConfigManager.count(
                            QueryChain.of(SysOssConfigEntity.class).where(SysOssConfigEntity::getStatus).eq(0)
                    );
                    if (activeCount <= 1) {
                        throw new BusinessException("系统中必须保留至少一个启用的存储配置");
                    }
                }
            }
        }
        
        sysOssConfigManager.saveOrUpdate(entity);
        
        // 4. 刷新缓存客户端
        // 如果是启用状态，则加载；否则尝试移除缓存
        if (entity.getStatus() == 0) {
            OssFactory.init(toProperties(entity));
        } else {
            OssFactory.remove(entity.getConfigKey());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteConfig(Long id) {
        SysOssConfigEntity config = sysOssConfigManager.getById(id);
        if (config != null) {
            sysOssConfigManager.removeById(id);
            OssFactory.remove(config.getConfigKey());
        }
    }

    @Override
    public SysOssConfigEntity getByConfigKey(String configKey) {
        return sysOssConfigManager.getByConfigKey(configKey);
    }

    @Override
    public List<SysOssConfigEntity> listAll() {
        return sysOssConfigManager.list();
    }

    private OssProperties toProperties(SysOssConfigEntity entity) {
        return BeanUtil.copyProperties(entity, OssProperties.class);
    }
}

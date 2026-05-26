package cn.muziseo.service.system.module.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.muziseo.common.core.datatracer.DataTracerTypeEnum;
import cn.muziseo.common.log.utils.DataTracerUtils;
import cn.muziseo.common.core.exception.BusinessException;
import cn.muziseo.common.core.enums.CommonStatus;
import cn.muziseo.common.oss.entity.OssProperties;
import cn.muziseo.common.oss.factory.OssFactory;
import cn.muziseo.service.system.enums.OssErrorCode;
import cn.muziseo.service.system.module.system.manager.SysOssConfigManager;
import cn.muziseo.service.system.module.system.repository.entity.SysOssConfigEntity;
import cn.muziseo.service.system.module.system.service.SysOssConfigService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import cn.muziseo.common.oss.core.OssClient;
import cn.muziseo.common.oss.entity.OssProperties;
import cn.muziseo.common.oss.factory.OssFactory;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.update.UpdateChain;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
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
    /**
     * 初始化 OSS 客户端工厂
     * <p>
     * 项目启动时自动执行，加载所有启用的存储配置
     */
    @PostConstruct
    @Override
    public void initOssFactory() {
        log.info("开始初始化 OSS 客户端工厂...");
        OssFactory.clear(); // 先清理旧缓存，确保配置更新生效
        List<SysOssConfigEntity> list = sysOssConfigManager.listEnabledConfig();
        for (SysOssConfigEntity config : list) {
            String configKey = config.getConfigKey();
            log.info("检查并初始化 OSS 配置: {}", configKey);
            OssFactory.init(toProperties(config));
        }
        log.info("OSS 客户端工厂初始化完成，共加载 {} 个配置", list.size());
    }

    /**
     * 重新加载指定配置的客户端
     *
     * @param configKey 配置 Key
     */
    @Override
    public void reloadClient(String configKey) {
        SysOssConfigEntity config = sysOssConfigManager.getByConfigKey(configKey);
        if (config == null || CommonStatus.isDisable(config.getStatus())) {
            OssFactory.remove(configKey);
        } else {
            OssFactory.init(toProperties(config));
        }
    }

    /**
     * 保存或更新存储配置
     * <p>
     * 1. 检查 Key 是否重复
     * 2. 状态切换逻辑：启用某配置时将其他配置设为停用
     * 3. 刷新 OSS 客户端工厂缓存
     *
     * @param entity 存储配置实体
     */
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
            entity.setStatus(CommonStatus.DISABLE.getValue());
        }

        // 3. 状态切换逻辑
        if (CommonStatus.isNormal(entity.getStatus())) {
            // 如果要启用当前配置，则将所有其他配置设为停用 (保持排他性)
            UpdateChain.of(SysOssConfigEntity.class)
                    .set(SysOssConfigEntity::getStatus, CommonStatus.DISABLE.getValue())
                    .where(SysOssConfigEntity::getStatus).eq(CommonStatus.NORMAL.getValue())
                    .and(SysOssConfigEntity::getId).ne(entity.getId())
                    .update();
        } else {
            // 如果尝试停用当前配置，需检查是否是最后一个启用的
            if (entity.getId() != null) {
                SysOssConfigEntity old = sysOssConfigManager.getById(entity.getId());
                // 如果原本是启用的，现在要改为停用
                if (old != null && CommonStatus.isNormal(old.getStatus())) {
                    long activeCount = sysOssConfigManager.count(
                            QueryChain.of(SysOssConfigEntity.class).where(SysOssConfigEntity::getStatus).eq(CommonStatus.NORMAL.getValue())
                    );
                    if (activeCount <= 1) {
                        throw new BusinessException("系统中必须保留至少一个启用的存储配置");
                    }
                }
            }
        }
        
        boolean isNew = entity.getId() == null;
        SysOssConfigEntity oldConfig = !isNew ? sysOssConfigManager.getById(entity.getId()) : null;

        sysOssConfigManager.saveOrUpdate(entity);
        
        if (isNew) {
            DataTracerUtils.insert(entity.getId(), DataTracerTypeEnum.OSS_CONFIG);
        } else {
            SysOssConfigEntity updatedConfig = sysOssConfigManager.getById(entity.getId());
            DataTracerUtils.update(entity.getId(), DataTracerTypeEnum.OSS_CONFIG, oldConfig, updatedConfig);
        }
        
        // 4. 刷新缓存：全量重载是最稳妥的方式，确保排他性启用状态和秘钥更新立即生效
        initOssFactory();
    }

    /**
     * 删除存储配置
     *
     * @param id 配置 ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteConfig(Long id) {
        SysOssConfigEntity config = sysOssConfigManager.getById(id);
        if (config != null) {
            sysOssConfigManager.removeById(id);
            OssFactory.remove(config.getConfigKey());
            DataTracerUtils.delete(id, DataTracerTypeEnum.OSS_CONFIG);
        }
    }

    /**
     * 根据配置 Key 获取存储配置
     *
     * @param configKey 配置 Key
     * @return 存储配置实体
     */
    @Override
    public SysOssConfigEntity getByConfigKey(String configKey) {
        return sysOssConfigManager.getByConfigKey(configKey);
    }

    /**
     * 获取所有存储配置列表
     *
     * @return 存储配置实体列表
     */
    @Override
    public List<SysOssConfigEntity> listAll() {
        return sysOssConfigManager.list();
    }

    /**
     * 测试存储配置有效性
     * <p>
     * 尝试创建临时客户端并上传测试文件
     *
     * @param entity 待测试的存储配置实体
     */
    @Override
    public void testConfig(SysOssConfigEntity entity) {
        OssProperties properties = toProperties(entity);
        try {
            // 1. 创建临时客户端
            OssClient client = OssFactory.create(properties);
            // 2. 尝试上传一个测试小文件
            String testContent = "Test OSS Connection - " + System.currentTimeMillis();
            byte[] bytes = testContent.getBytes(StandardCharsets.UTF_8);
            String path = (properties.getPrefix() != null ? properties.getPrefix() : "") + "test_connection.txt";
            
            client.upload(bytes, path, "text/plain");
            log.info("OSS 配置测试成功: service={}, endpoint={}", entity.getService(), entity.getEndpoint());
        } catch (Exception e) {
            log.error("OSS 配置测试失败: {}", e.getMessage(), e);
            throw new BusinessException("连接测试失败: " + e.getMessage());
        }
    }

    private OssProperties toProperties(SysOssConfigEntity entity) {
        return BeanUtil.copyProperties(entity, OssProperties.class);
    }
}

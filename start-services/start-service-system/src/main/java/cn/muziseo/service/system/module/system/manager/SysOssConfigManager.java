package cn.muziseo.service.system.module.system.manager;

import cn.muziseo.common.db.service.impl.BaseServiceImpl;
import cn.muziseo.service.system.module.system.repository.entity.SysOssConfigEntity;
import cn.muziseo.service.system.module.system.repository.mapper.SysOssConfigMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * OSS 存储配置管理 Manager 层
 * <p>
 * 处理对象存储（OSS）策略配置的持久化逻辑，支持多存储源配置及状态维护。
 *
 * @author 木子软件
 */
@Service
public class SysOssConfigManager extends BaseServiceImpl<SysOssConfigMapper, SysOssConfigEntity> {

    /**
     * 获取所有处于启用状态的 OSS 配置列表
     *
     * @return 启用的配置实体列表
     */
    public List<SysOssConfigEntity> listEnabledConfig() {
        return queryChain()
                .where(SysOssConfigEntity::getStatus).eq(0)
                .list();
    }

    /**
     * 根据配置键名获取特定的 OSS 配置详情
     *
     * @param configKey 配置键名（如：minio, qiniu, aliyun）
     * @return OSS 配置实体，如果不存在则返回 null
     */
    public SysOssConfigEntity getByConfigKey(String configKey) {
        return queryChain()
                .where(SysOssConfigEntity::getConfigKey).eq(configKey)
                .one();
    }
}

package cn.muziseo.service.system.module.system.manager;

import cn.muziseo.common.db.service.impl.BaseServiceImpl;
import cn.muziseo.service.system.module.system.repository.entity.SysOssConfigEntity;
import cn.muziseo.service.system.module.system.repository.mapper.SysOssConfigMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * OSS 存储配置 Manager
 *
 * @author 木子软件
 */
@Service
public class SysOssConfigManager extends BaseServiceImpl<SysOssConfigMapper, SysOssConfigEntity> {

    /**
     * 获取所有启用的配置
     */
    public List<SysOssConfigEntity> listEnabledConfig() {
        return queryChain()
                .where(SysOssConfigEntity::getStatus).eq(0)
                .list();
    }

    /**
     * 根据 Key 获取配置
     */
    public SysOssConfigEntity getByConfigKey(String configKey) {
        return queryChain()
                .where(SysOssConfigEntity::getConfigKey).eq(configKey)
                .one();
    }
}

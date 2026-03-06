package cn.muziseo.service.system.module.system.manager;

import cn.muziseo.common.db.service.impl.BaseServiceImpl;
import cn.muziseo.service.system.module.system.repository.entity.SystemConfigEntity;
import cn.muziseo.service.system.module.system.repository.mapper.SystemConfigMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Service;

/**
 * 系统配置 Manager 层
 *
 * @author 木子软件
 * @Date 2026-02-27
 */
@Service
public class SystemConfigManager extends BaseServiceImpl<SystemConfigMapper, SystemConfigEntity> {

    /**
     * 根据配置键获取配置
     */
    public SystemConfigEntity getByConfigKey(String configKey) {
        return queryChain()
                .where(SystemConfigEntity::getConfigKey).eq(configKey)
                .one();
    }

    /**
     * 检查配置键是否存在
     */
    public boolean existsByConfigKey(String configKey) {
        return exists(QueryWrapper.create()
                .where(SystemConfigEntity::getConfigKey).eq(configKey));
    }
}

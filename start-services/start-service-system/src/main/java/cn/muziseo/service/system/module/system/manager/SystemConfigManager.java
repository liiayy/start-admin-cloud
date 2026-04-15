package cn.muziseo.service.system.module.system.manager;

import cn.muziseo.common.db.service.impl.BaseServiceImpl;
import cn.muziseo.service.system.module.system.controller.request.SystemConfigPageRequest;
import cn.muziseo.service.system.module.system.repository.entity.SystemConfigEntity;
import cn.muziseo.service.system.module.system.repository.mapper.SystemConfigMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Service;

/**
 * 系统参数 Manager 层
 *
 * @author 木子软件
 * @Date 2026-02-27
 */
@Service
public class SystemConfigManager extends BaseServiceImpl<SystemConfigMapper, SystemConfigEntity> {

    /**
     * 根据参数键名获取配置
     */
    public SystemConfigEntity getByConfigKey(String configKey) {
        return queryChain()
                .where(SystemConfigEntity::getConfigKey).eq(configKey)
                .one();
    }

    /**
     * 检查参数键名是否存在
     */
    public boolean existsByConfigKey(String configKey) {
        return exists(QueryWrapper.create()
                .where(SystemConfigEntity::getConfigKey).eq(configKey));
    }

    /**
     * 分页查询系统参数
     */
    public Page<SystemConfigEntity> pageConfig(SystemConfigPageRequest request) {
        QueryWrapper wrapper = QueryWrapper.create()
                .where(SystemConfigEntity::getName).like(request.getName(), request.getName() != null)
                .and(SystemConfigEntity::getConfigKey).like(request.getConfigKey(), request.getConfigKey() != null)
                .orderBy(SystemConfigEntity::getId, false);

        return page(new Page<>(request.getPageNum(), request.getPageSize()), wrapper);
    }
}

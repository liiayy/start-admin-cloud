package cn.muziseo.service.system.module.system.manager;

import cn.muziseo.common.db.service.impl.BaseServiceImpl;
import cn.muziseo.service.system.module.system.controller.request.SystemConfigPageRequest;
import cn.muziseo.service.system.module.system.repository.entity.SystemConfigEntity;
import cn.muziseo.service.system.module.system.repository.mapper.SystemConfigMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Service;

/**
 * 系统参数配置管理 Manager 层
 * <p>
 * 处理系统运行所需的动态参数配置，提供按键名查询、唯一性校验及分页管理功能。
 *
 * @author 木子软件
 */
@Service
public class SystemConfigManager extends BaseServiceImpl<SystemConfigMapper, SystemConfigEntity> {

    /**
     * 根据参数键名查询配置信息
     *
     * @param configKey 参数键名（如：sys.user.initPassword）
     * @return 系统参数实体信息，如果不存在则返回 null
     */
    public SystemConfigEntity getByConfigKey(String configKey) {
        return queryChain()
                .where(SystemConfigEntity::getConfigKey).eq(configKey)
                .one();
    }

    /**
     * 校验参数键名是否已存在
     *
     * @param configKey 参数键名
     * @return true 表示已存在，false 表示不存在
     */
    public boolean existsByConfigKey(String configKey) {
        return exists(QueryWrapper.create()
                .where(SystemConfigEntity::getConfigKey).eq(configKey));
    }

    /**
     * 分页查询系统参数列表
     *
     * @param request 分页及筛选条件（支持参数名称、键名模糊查询）
     * @return 分页结果对象
     */
    public Page<SystemConfigEntity> pageConfig(SystemConfigPageRequest request) {
        QueryWrapper wrapper = QueryWrapper.create()
                .where(SystemConfigEntity::getName).like(request.getName(), request.getName() != null)
                .and(SystemConfigEntity::getConfigKey).like(request.getConfigKey(), request.getConfigKey() != null)
                .orderBy(SystemConfigEntity::getId, false);

        return page(new Page<>(request.getPageNum(), request.getPageSize()), wrapper);
    }
}

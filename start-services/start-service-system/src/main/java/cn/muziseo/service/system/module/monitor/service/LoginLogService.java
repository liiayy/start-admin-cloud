package cn.muziseo.service.system.module.monitor.service;

import cn.muziseo.common.db.page.PageResponse;
import cn.muziseo.service.system.module.monitor.repository.entity.LoginLogEntity;

/**
 * 登录日志服务
 */
public interface LoginLogService {

    /**
     * 分页查询登录日志
     */
    PageResponse<LoginLogEntity> page(int pageNum, int pageSize, LoginLogEntity query);

    /**
     * 批量删除
     */
    void deleteByIds(Long[] ids);

    /**
     * 清空日志
     */
    void clean();
}

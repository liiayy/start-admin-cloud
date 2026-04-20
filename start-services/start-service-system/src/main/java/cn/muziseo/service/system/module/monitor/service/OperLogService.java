package cn.muziseo.service.system.module.monitor.service;

import cn.muziseo.common.db.page.PageResponse;
import cn.muziseo.service.system.module.monitor.repository.entity.OperLogEntity;
import com.mybatisflex.core.query.QueryWrapper;

/**
 * 操作日志服务
 */
public interface OperLogService {
    
    /**
     * 分页查询操作日志
     */
    PageResponse<OperLogEntity> page(int pageNum, int pageSize, OperLogEntity query);

    /**
     * 保存操作日志
     */
    void save(OperLogEntity entity);

    /**
     * 根据 ID 获取详情
     */
    OperLogEntity getById(Long id);

    /**
     * 批量删除
     */
    void deleteByIds(Long[] ids);

    /**
     * 清空日志
     */
    void clean();
}

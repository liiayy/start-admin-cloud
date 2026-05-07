package cn.muziseo.service.system.module.monitor.service;

import cn.muziseo.common.core.event.ErrorLogEvent;
import cn.muziseo.common.db.page.PageResponse;
import cn.muziseo.service.system.module.monitor.repository.entity.SysErrorLogEntity;

/**
 * 错误日志服务接口
 */
public interface SysErrorLogService {

    /**
     * 保存错误日志
     */
    void saveErrorLog(ErrorLogEvent event);

    /**
     * 分页查询
     */
    PageResponse<SysErrorLogEntity> page(int pageNum, int pageSize, SysErrorLogEntity query);

    /**
     * 批量删除
     */
    void deleteByIds(Long[] ids);

    /**
     * 清空日志
     */
    void clean();
    
    /**
     * 获取详情
     */
    SysErrorLogEntity getById(Long id);

    /**
     * 更新处理状态
     */
    void updateHandleStatus(Long id, Integer status, String remark);
}

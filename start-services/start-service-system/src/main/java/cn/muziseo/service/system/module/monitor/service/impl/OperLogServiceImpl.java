package cn.muziseo.service.system.module.monitor.service.impl;

import cn.muziseo.common.db.page.PageResponse;
import cn.muziseo.service.system.module.monitor.manager.OperLogManager;
import cn.muziseo.service.system.module.monitor.repository.entity.OperLogEntity;
import cn.muziseo.service.system.module.monitor.service.OperLogService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Db;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * 操作日志服务实现
 */
@Service
@RequiredArgsConstructor
public class OperLogServiceImpl implements OperLogService {

    private final OperLogManager operLogManager;

    /**
     * 分页查询操作日志
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @param query    查询条件
     * @return 分页结果
     */
    @Override
    public PageResponse<OperLogEntity> page(int pageNum, int pageSize, OperLogEntity query) {
        Page<OperLogEntity> page = operLogManager.pageLog(pageNum, pageSize, query);
        PageResponse<OperLogEntity> response = new PageResponse<>();
        response.setList(page.getRecords());
        response.setTotal(page.getTotalRow());
        return response;
    }

    /**
     * 保存操作日志
     *
     * @param entity 操作日志实体
     */
    @Override
    public void save(OperLogEntity entity) {
        operLogManager.save(entity);
    }

    /**
     * 根据 ID 获取操作日志详情
     *
     * @param id 日志 ID
     * @return 操作日志实体
     */
    @Override
    public OperLogEntity getById(Long id) {
        return operLogManager.getById(id);
    }

    /**
     * 批量删除操作日志
     *
     * @param ids 日志 ID 数组
     */
    @Override
    public void deleteByIds(Long[] ids) {
        operLogManager.removeByIds(Arrays.asList(ids));
    }

    /**
     * 清空操作日志
     */
    @Override
    public void clean() {
        Db.updateBySql("TRUNCATE TABLE system_oper_log");
    }
}

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

    @Override
    public PageResponse<OperLogEntity> page(int pageNum, int pageSize, OperLogEntity query) {
        Page<OperLogEntity> page = operLogManager.pageLog(pageNum, pageSize, query);
        PageResponse<OperLogEntity> response = new PageResponse<>();
        response.setList(page.getRecords());
        response.setTotal(page.getTotalRow());
        return response;
    }

    @Override
    public void save(OperLogEntity entity) {
        operLogManager.save(entity);
    }

    @Override
    public OperLogEntity getById(Long id) {
        return operLogManager.getById(id);
    }

    @Override
    public void deleteByIds(Long[] ids) {
        operLogManager.removeByIds(Arrays.asList(ids));
    }

    @Override
    public void clean() {
        Db.updateBySql("TRUNCATE TABLE system_oper_log");
    }
}

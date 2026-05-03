package cn.muziseo.service.system.module.monitor.manager;

import cn.muziseo.common.db.service.impl.BaseServiceImpl;
import cn.muziseo.service.system.module.monitor.repository.entity.OperLogEntity;
import cn.muziseo.service.system.module.monitor.repository.mapper.OperLogMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Service;

/**
 * 操作日志管理 Manager 层
 * <p>
 * 处理系统操作日志的持久化，提供分页查询及审计日志记录功能。
 *
 * @author 木子软件
 */
@Service
public class OperLogManager extends BaseServiceImpl<OperLogMapper, OperLogEntity> {

    /**
     * 分页查询操作日志
     *
     * @param pageNum  当前页码
     * @param pageSize 每页显示数量
     * @param query    筛选条件实体
     * @return 分页结果对象
     */
    public Page<OperLogEntity> pageLog(int pageNum, int pageSize, OperLogEntity query) {
        QueryWrapper wrapper = QueryWrapper.create()
                .where(OperLogEntity::getTitle).like(query.getTitle(), query.getTitle() != null)
                .and(OperLogEntity::getBusinessType).eq(query.getBusinessType(), query.getBusinessType() != null)
                .and(OperLogEntity::getStatus).eq(query.getStatus(), query.getStatus() != null)
                .and(OperLogEntity::getOperName).like(query.getOperName(), query.getOperName() != null)
                .orderBy(OperLogEntity::getId, false);
        return page(new Page<>(pageNum, pageSize), wrapper);
    }
}

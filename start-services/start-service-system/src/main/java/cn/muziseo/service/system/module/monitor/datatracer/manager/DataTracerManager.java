package cn.muziseo.service.system.module.monitor.datatracer.manager;

import cn.muziseo.common.db.service.impl.BaseServiceImpl;
import cn.muziseo.service.system.module.monitor.datatracer.repository.entity.DataTracerEntity;
import cn.muziseo.service.system.module.monitor.datatracer.repository.mapper.DataTracerMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Service;

/**
 * 数据变更记录 Manager 层
 */
@Service
public class DataTracerManager extends BaseServiceImpl<DataTracerMapper, DataTracerEntity> {

    /**
     * 根据业务ID和类型分页查询变更记录
     */
    public Page<DataTracerEntity> pageByDataIdAndType(Long dataId, Integer type, int pageNum, int pageSize) {
        QueryWrapper wrapper = QueryWrapper.create()
                .where(DataTracerEntity::getDataId).eq(dataId)
                .and(DataTracerEntity::getType).eq(type)
                .orderBy(DataTracerEntity::getCreateTime, false);
        return page(new Page<>(pageNum, pageSize), wrapper);
    }

    /**
     * 高级分页查询
     */
    public Page<DataTracerEntity> pageTracer(cn.muziseo.service.system.module.monitor.datatracer.controller.request.DataTracerPageRequest request) {
        QueryWrapper wrapper = QueryWrapper.create()
                .where(DataTracerEntity::getDataId).eq(request.getDataId(), request.getDataId() != null)
                .and(DataTracerEntity::getType).eq(request.getType(), request.getType() != null)
                .and(DataTracerEntity::getOperName).like(request.getOperName(), cn.hutool.core.util.StrUtil.isNotBlank(request.getOperName()))
                .and(DataTracerEntity::getCreateTime).ge(request.getBeginTime(), request.getBeginTime() != null)
                .and(DataTracerEntity::getCreateTime).le(request.getEndTime(), request.getEndTime() != null)
                .orderBy(DataTracerEntity::getCreateTime, false);
        return page(new Page<>(request.getPageNum(), request.getPageSize()), wrapper);
    }
}

package cn.muziseo.service.system.module.monitor.datatracer.service;

import cn.muziseo.common.core.datatracer.DataTracerForm;
import cn.muziseo.common.core.datatracer.DataTracerTypeEnum;
import cn.muziseo.common.db.page.PageResponse;
import cn.muziseo.service.system.module.monitor.datatracer.repository.entity.DataTracerEntity;

/**
 * 数据变更记录服务
 */
public interface DataTracerService {

    /**
     * 新增记录
     */
    void insert(Long dataId, DataTracerTypeEnum type);

    /**
     * 修改记录（自动比对差异）
     */
    void update(Long dataId, DataTracerTypeEnum type, Object oldBean, Object newBean);

    /**
     * 删除记录
     */
    void delete(Long dataId, DataTracerTypeEnum type);

    /**
     * 批量删除记录
     */
    void batchDelete(Long[] dataIds, DataTracerTypeEnum type);

    /**
     * 通用记录方法
     */
    void addTrace(DataTracerForm form);

    /**
     * 保存跨微服务数据变更事件
     */
    void addTrace(cn.muziseo.common.core.event.DataTracerEvent event);

    /**
     * 分页查询
     */
    PageResponse<DataTracerEntity> page(Long dataId, Integer type, int pageNum, int pageSize);

    /**
     * 高级分页查询
     */
    PageResponse<cn.muziseo.service.system.module.monitor.datatracer.controller.vo.DataTracerVO> pageTracer(cn.muziseo.service.system.module.monitor.datatracer.controller.request.DataTracerPageRequest request);

    /**
     * 根据主键批量删除
     */
    void deleteByIds(java.util.List<Long> ids);

    /**
     * 清空全部
     */
    void clean();
}

package cn.muziseo.service.system.module.monitor.datatracer.repository.mapper;

import cn.muziseo.service.system.module.monitor.datatracer.repository.entity.DataTracerEntity;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据变更记录 Mapper
 */
@Mapper
public interface DataTracerMapper extends BaseMapper<DataTracerEntity> {
}

package cn.muziseo.service.system.module.monitor.repository.mapper;

import cn.muziseo.service.system.module.monitor.repository.entity.OperLogEntity;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 操作日志 Mapper 接口
 */
@Mapper
public interface OperLogMapper extends BaseMapper<OperLogEntity> {
}

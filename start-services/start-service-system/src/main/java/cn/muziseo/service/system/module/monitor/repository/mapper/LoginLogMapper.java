package cn.muziseo.service.system.module.monitor.repository.mapper;

import cn.muziseo.service.system.module.monitor.repository.entity.LoginLogEntity;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 登录日志 Mapper 接口
 */
@Mapper
public interface LoginLogMapper extends BaseMapper<LoginLogEntity> {
}

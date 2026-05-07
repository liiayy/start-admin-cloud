package cn.muziseo.service.system.module.monitor.manager;

import cn.muziseo.common.db.service.impl.BaseServiceImpl;
import cn.muziseo.service.system.module.monitor.repository.entity.SysErrorLogEntity;
import cn.muziseo.service.system.module.monitor.repository.mapper.SysErrorLogMapper;
import org.springframework.stereotype.Service;

/**
 * 错误日志管理 Manager 层
 */
@Service
public class SysErrorLogManager extends BaseServiceImpl<SysErrorLogMapper, SysErrorLogEntity> {
}

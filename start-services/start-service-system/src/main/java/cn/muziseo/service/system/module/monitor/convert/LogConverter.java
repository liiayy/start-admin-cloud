package cn.muziseo.service.system.module.monitor.convert;

import cn.muziseo.common.core.event.LoginLogEvent;
import cn.muziseo.common.core.event.OperLogEvent;
import cn.muziseo.service.system.module.monitor.repository.entity.LoginLogEntity;
import cn.muziseo.service.system.module.monitor.repository.entity.OperLogEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface LogConverter {
    void copyToEntity(LoginLogEvent event, @MappingTarget LoginLogEntity entity);
    void copyToEntity(OperLogEvent event, @MappingTarget OperLogEntity entity);
}

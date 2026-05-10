package cn.muziseo.service.system.module.system.convert;

import cn.muziseo.service.system.module.system.controller.request.SystemConfigCreateRequest;
import cn.muziseo.service.system.module.system.controller.vo.SystemConfigVO;
import cn.muziseo.service.system.module.system.repository.entity.SystemConfigEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SystemConfigConverter {
    SystemConfigVO toVO(SystemConfigEntity entity);
    SystemConfigEntity toEntity(SystemConfigCreateRequest request);
}

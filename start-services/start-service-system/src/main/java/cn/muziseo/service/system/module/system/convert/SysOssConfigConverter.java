package cn.muziseo.service.system.module.system.convert;

import cn.muziseo.service.system.module.system.controller.request.SysOssConfigAddRequest;
import cn.muziseo.service.system.module.system.controller.vo.SysOssConfigVO;
import cn.muziseo.service.system.module.system.repository.entity.SysOssConfigEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SysOssConfigConverter {
    SysOssConfigVO toVO(SysOssConfigEntity entity);
    SysOssConfigEntity toEntity(SysOssConfigAddRequest request);
}

package cn.muziseo.service.system.module.system.convert;

import cn.muziseo.service.system.module.system.controller.vo.SysOssVO;
import cn.muziseo.service.system.module.system.repository.entity.SysOssEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SysOssConverter {
    SysOssVO toVO(SysOssEntity entity);
}

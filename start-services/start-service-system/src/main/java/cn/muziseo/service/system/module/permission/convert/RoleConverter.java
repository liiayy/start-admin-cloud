package cn.muziseo.service.system.module.permission.convert;

import cn.muziseo.service.system.module.permission.controller.request.RoleAddRequest;
import cn.muziseo.service.system.module.permission.controller.request.RoleUpdateRequest;
import cn.muziseo.service.system.module.permission.controller.vo.RoleVO;
import cn.muziseo.service.system.module.permission.repository.entity.RoleEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleConverter {
    RoleVO toVO(RoleEntity entity);
    RoleEntity toEntity(RoleAddRequest request);
    RoleEntity toEntity(RoleUpdateRequest request);
}

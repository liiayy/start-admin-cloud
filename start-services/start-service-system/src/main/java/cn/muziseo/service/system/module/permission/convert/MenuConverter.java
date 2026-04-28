package cn.muziseo.service.system.module.permission.convert;

import cn.muziseo.service.system.module.permission.controller.request.MenuAddRequest;
import cn.muziseo.service.system.module.permission.controller.request.MenuUpdateRequest;
import cn.muziseo.service.system.module.permission.controller.vo.MenuVO;
import cn.muziseo.service.system.module.permission.controller.vo.MenuTreeVO;
import cn.muziseo.service.system.module.permission.repository.entity.MenuEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MenuConverter {
    MenuVO toVO(MenuEntity entity);
    MenuTreeVO toTreeVO(MenuEntity entity);
    MenuEntity toEntity(MenuAddRequest request);
    MenuEntity toEntity(MenuUpdateRequest request);
}

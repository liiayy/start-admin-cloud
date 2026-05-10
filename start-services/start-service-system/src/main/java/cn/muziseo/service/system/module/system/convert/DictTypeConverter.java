package cn.muziseo.service.system.module.system.convert;

import cn.muziseo.service.system.module.system.controller.request.DictTypeCreateRequest;
import cn.muziseo.service.system.module.system.controller.vo.DictTypeVO;
import cn.muziseo.service.system.module.system.repository.entity.DictTypeEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DictTypeConverter {
    DictTypeVO toVO(DictTypeEntity entity);
    DictTypeEntity toEntity(DictTypeCreateRequest request);
}

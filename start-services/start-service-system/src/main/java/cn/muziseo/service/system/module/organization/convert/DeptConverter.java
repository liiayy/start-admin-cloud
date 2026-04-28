package cn.muziseo.service.system.module.organization.convert;

import cn.muziseo.service.system.module.organization.api.dto.DeptRemoteDTO;
import cn.muziseo.service.system.module.organization.controller.request.DeptAddRequest;
import cn.muziseo.service.system.module.organization.controller.vo.DeptVO;
import cn.muziseo.service.system.module.organization.controller.vo.DeptTreeVO;
import cn.muziseo.service.system.module.organization.repository.entity.DeptEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DeptConverter {
    DeptVO toVO(DeptEntity entity);
    DeptTreeVO toTreeVO(DeptEntity entity);
    DeptEntity toEntity(DeptAddRequest request);
    DeptRemoteDTO toRemoteDTO(DeptEntity entity);
}

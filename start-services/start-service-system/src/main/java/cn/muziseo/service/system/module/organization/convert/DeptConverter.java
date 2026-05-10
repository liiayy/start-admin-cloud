package cn.muziseo.service.system.module.organization.convert;

import cn.muziseo.service.system.module.organization.api.dto.DeptRemoteDTO;
import cn.muziseo.service.system.module.organization.controller.request.DeptCreateRequest;
import cn.muziseo.service.system.module.organization.controller.vo.DeptVO;
import cn.muziseo.service.system.module.organization.controller.vo.DeptTreeVO;
import cn.muziseo.service.system.module.organization.repository.entity.DeptEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DeptConverter {
    DeptVO toVO(DeptEntity entity);
    DeptTreeVO toTreeVO(DeptEntity entity);
    DeptEntity toEntity(DeptCreateRequest request);
    DeptRemoteDTO toRemoteDTO(DeptEntity entity);
}

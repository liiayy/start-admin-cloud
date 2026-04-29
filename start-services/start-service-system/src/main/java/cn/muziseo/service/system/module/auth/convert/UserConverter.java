package cn.muziseo.service.system.module.auth.convert;

import cn.muziseo.common.core.domain.dto.DataScopeInfo;
import cn.muziseo.service.system.module.auth.api.dto.DataScopeRemoteDTO;
import cn.muziseo.service.system.module.auth.api.dto.UserRemoteDTO;
import cn.muziseo.service.system.module.auth.controller.request.UserAddRequest;
import cn.muziseo.service.system.module.auth.controller.request.UserProfileUpdateRequest;
import cn.muziseo.service.system.module.auth.controller.request.UserUpdateRequest;
import cn.muziseo.service.system.module.auth.controller.vo.UserImportVO;
import cn.muziseo.service.system.module.auth.controller.vo.UserVO;
import cn.muziseo.service.system.module.auth.repository.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserConverter {
    UserEntity toEntity(UserAddRequest request);
    UserEntity toEntity(UserUpdateRequest request);
    UserUpdateRequest toUpdateRequest(UserProfileUpdateRequest request);
    UserEntity toEntity(UserImportVO vo);
    UserVO toVO(UserEntity entity);
    UserRemoteDTO toRemoteDTO(UserEntity entity);
    void copyToEntity(UserImportVO vo, @org.mapstruct.MappingTarget UserEntity entity);
    DataScopeRemoteDTO toRemoteDTO(DataScopeInfo info);
}

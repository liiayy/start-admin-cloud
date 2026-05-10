package cn.muziseo.service.system.module.organization.convert;

import cn.muziseo.service.system.module.organization.controller.request.PostCreateRequest;
import cn.muziseo.service.system.module.organization.controller.vo.PostVO;
import cn.muziseo.service.system.module.organization.repository.entity.PostEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PostConverter {
    PostVO toVO(PostEntity entity);
    PostEntity toEntity(PostCreateRequest request);
}

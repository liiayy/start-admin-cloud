package cn.muziseo.service.system.module.notice.convert;

import cn.muziseo.service.system.module.notice.controller.request.NoticeCreateRequest;
import cn.muziseo.service.system.module.notice.controller.request.NoticeUpdateRequest;
import cn.muziseo.service.system.module.notice.controller.vo.NoticeVO;
import cn.muziseo.service.system.module.notice.repository.entity.NoticeEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NoticeConverter {
    NoticeVO toVO(NoticeEntity entity);
    NoticeEntity toEntity(NoticeCreateRequest request);
    NoticeEntity toEntity(NoticeUpdateRequest request);
}

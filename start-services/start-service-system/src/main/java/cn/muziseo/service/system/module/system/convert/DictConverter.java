package cn.muziseo.service.system.module.system.convert;

import cn.muziseo.common.core.domain.dto.DictDataSimpleDTO;
import cn.muziseo.service.system.module.system.controller.request.DictDataAddRequest;
import cn.muziseo.service.system.module.system.controller.vo.DictDataVO;
import cn.muziseo.service.system.module.system.repository.entity.DictEntity;
import org.mapstruct.Mapper;

/**
 * 字典模块对象转换器
 *
 * @author 木子软件
 */
@Mapper(componentModel = "spring")
public interface DictConverter {

    /**
     * Entity 转 VO
     */
    DictDataVO toVO(DictEntity entity);

    /**
     * Entity 转 SimpleDTO
     */
    DictDataSimpleDTO toSimpleDTO(DictEntity entity);

    /**
     * Request 转 Entity
     */
    DictEntity toEntity(DictDataAddRequest request);
}

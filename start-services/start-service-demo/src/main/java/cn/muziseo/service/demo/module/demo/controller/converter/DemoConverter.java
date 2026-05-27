package cn.muziseo.service.demo.module.demo.controller.converter;

import cn.muziseo.service.demo.module.demo.controller.request.DemoAddRequest;
import cn.muziseo.service.demo.module.demo.controller.vo.DemoVO;
import cn.muziseo.service.demo.module.demo.repository.entity.DemoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 演示产品 MapStruct 转换器
 *
 * @author 木子软件
 */
@Mapper(componentModel = "spring")
public interface DemoConverter {

    /**
     * Entity 转 VO
     *
     * @param entity 实体类
     * @return 视图对象
     */
    DemoVO toVO(DemoEntity entity);

    /**
     * DTO 转 Entity
     *
     * @param request 请求参数
     * @return 实体类
     */
    @Mapping(target = "id", ignore = true)
    DemoEntity toEntity(DemoAddRequest request);
}

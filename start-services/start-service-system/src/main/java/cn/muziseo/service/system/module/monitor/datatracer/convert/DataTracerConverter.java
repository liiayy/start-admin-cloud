package cn.muziseo.service.system.module.monitor.datatracer.convert;

import cn.muziseo.service.system.module.monitor.datatracer.controller.vo.DataTracerVO;
import cn.muziseo.service.system.module.monitor.datatracer.repository.entity.DataTracerEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DataTracerConverter {

    DataTracerVO toVO(DataTracerEntity entity);

}

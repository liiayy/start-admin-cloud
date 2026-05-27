package cn.muziseo.service.demo.module.demo.repository.mapper;

import cn.muziseo.service.demo.module.demo.repository.entity.DemoEntity;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 演示 Mapper 接口
 *
 * @author Antigravity
 */
@Mapper
public interface DemoMapper extends BaseMapper<DemoEntity> {

}

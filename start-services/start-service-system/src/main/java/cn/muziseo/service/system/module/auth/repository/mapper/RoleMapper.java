package cn.muziseo.service.system.module.auth.repository.mapper;

import cn.muziseo.service.system.module.auth.repository.entity.RoleEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色表 Mapper 接口
 */
@Mapper
public interface RoleMapper extends BaseMapper<RoleEntity> {
}

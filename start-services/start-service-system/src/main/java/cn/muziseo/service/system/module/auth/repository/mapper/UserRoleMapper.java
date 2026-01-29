package cn.muziseo.service.system.module.auth.repository.mapper;

import cn.muziseo.service.system.module.auth.repository.entity.UserRoleEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户角色关联 Mapper 接口
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRoleEntity> {
}

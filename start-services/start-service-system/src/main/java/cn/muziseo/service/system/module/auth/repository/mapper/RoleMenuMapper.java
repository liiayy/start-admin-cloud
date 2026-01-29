package cn.muziseo.service.system.module.auth.repository.mapper;

import cn.muziseo.service.system.module.auth.repository.entity.RoleMenuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色菜单关联 Mapper 接口
 */
@Mapper
public interface RoleMenuMapper extends BaseMapper<RoleMenuEntity> {
}

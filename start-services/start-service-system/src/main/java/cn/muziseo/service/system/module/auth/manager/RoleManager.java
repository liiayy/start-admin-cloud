package cn.muziseo.service.system.module.auth.manager;

import cn.muziseo.service.system.module.auth.repository.entity.RoleEntity;
import cn.muziseo.service.system.module.auth.repository.mapper.RoleMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 角色表 Manager 层
 */
@Service
public class RoleManager extends ServiceImpl<RoleMapper, RoleEntity> {
}

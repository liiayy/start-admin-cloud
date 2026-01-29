package cn.muziseo.service.system.module.auth.manager;

import cn.muziseo.service.system.module.auth.repository.entity.UserRoleEntity;
import cn.muziseo.service.system.module.auth.repository.mapper.UserRoleMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 用户角色关联 Manager 层
 */
@Service
public class UserRoleManager extends ServiceImpl<UserRoleMapper, UserRoleEntity> {
}

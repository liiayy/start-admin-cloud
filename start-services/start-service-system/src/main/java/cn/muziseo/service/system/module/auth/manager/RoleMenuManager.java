package cn.muziseo.service.system.module.auth.manager;

import cn.muziseo.service.system.module.auth.repository.entity.RoleMenuEntity;
import cn.muziseo.service.system.module.auth.repository.mapper.RoleMenuMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 角色菜单关联 Manager 层
 */
@Service
public class RoleMenuManager extends ServiceImpl<RoleMenuMapper, RoleMenuEntity> {
}

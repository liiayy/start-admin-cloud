package cn.muziseo.service.system.module.auth.manager;

import cn.muziseo.common.db.service.impl.BaseServiceImpl;
import cn.muziseo.service.system.module.auth.repository.entity.RoleMenuEntity;
import cn.muziseo.service.system.module.auth.repository.mapper.RoleMenuMapper;
import org.springframework.stereotype.Service;

/**
 * 角色菜单关联 Manager 层
 * <p>
 * 提供角色菜单关联表的基础数据库操作，继承 MyBatis-Plus 的 ServiceImpl
 *
 * @author 木子软件
 * @Date 2026-01-07
 * @Wechat liiayy
 * @Email 773582348@qq.com
 * @Copyright <a href="https://code.muziseo.cn">木子软件</a>
 */
@Service
public class RoleMenuManager extends BaseServiceImpl<RoleMenuMapper, RoleMenuEntity> {
}

package cn.muziseo.service.system.module.auth.manager;

import cn.muziseo.service.system.module.auth.repository.entity.UserRoleEntity;
import cn.muziseo.service.system.module.auth.repository.mapper.UserRoleMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 用户角色关联 Manager 层
 * <p>
 * 提供用户角色关联表的基础数据库操作，继承 MyBatis-Plus 的 ServiceImpl
 *
 * @author 木子软件
 * @Date 2026-01-07
 * @Wechat liiayy
 * @Email 773582348@qq.com
 * @Copyright <a href="https://code.muziseo.cn">木子软件</a>
 */
@Service
public class UserRoleManager extends ServiceImpl<UserRoleMapper, UserRoleEntity> {
}

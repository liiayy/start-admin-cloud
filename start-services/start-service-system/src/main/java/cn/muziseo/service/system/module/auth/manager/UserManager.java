package cn.muziseo.service.system.module.auth.manager;

import cn.muziseo.service.system.module.auth.repository.entity.UserEntity;
import cn.muziseo.service.system.module.auth.repository.mapper.UserMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 用户表 Manager 层
 */
@Service
public class UserManager extends ServiceImpl<UserMapper, UserEntity> {
}

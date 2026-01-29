package cn.muziseo.service.system.module.auth.service.impl;

import cn.muziseo.service.system.module.auth.controller.request.UserAddRequest;
import cn.muziseo.service.system.module.auth.manager.UserManager;
import cn.muziseo.service.system.module.auth.repository.entity.UserEntity;
import cn.muziseo.service.system.module.auth.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * 用户业务实现
 */
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserManager userManager;

    @Override
    public UserEntity getByUsername(String username) {
        return userManager.getOne(new LambdaQueryWrapper<UserEntity>()
                .eq(UserEntity::getUsername, username));
    }

    @Override
    public void addUser(UserAddRequest request) {
        UserEntity userEntity = cn.hutool.core.bean.BeanUtil.copyProperties(request, UserEntity.class);
        // 这里后续可以补充密码加密逻辑
        userManager.save(userEntity);
    }
}

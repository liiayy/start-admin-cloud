package cn.muziseo.service.system.module.auth.service.impl;

import cn.muziseo.service.system.module.auth.controller.request.UserAddRequest;
import cn.muziseo.service.system.module.auth.manager.UserManager;
import cn.muziseo.service.system.module.auth.repository.entity.UserEntity;
import cn.muziseo.service.system.module.auth.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 用户业务实现
 * <p>
 * 实现用户的增删改查、用户名查询等功能
 *
 * @author 木子软件
 * @Date 2026-01-29
 * @Wechat liiayy
 * @Email 773582348@qq.com
 * @Copyright <a href="https://code.muziseo.cn">木子软件</a>
 */
@Service
@Slf4j
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

    @Override
    public UserEntity getUserById(Long id) {
        return userManager.getById(id);
    }
}

package cn.muziseo.service.system.module.auth.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.muziseo.common.core.exception.BusinessException;
import cn.muziseo.service.system.module.auth.controller.request.LoginRequest;
import cn.muziseo.service.system.module.auth.repository.entity.UserEntity;
import cn.muziseo.service.system.module.auth.service.AuthService;
import cn.muziseo.service.system.module.auth.service.MenuService;
import cn.muziseo.service.system.module.auth.service.RoleService;
import cn.muziseo.service.system.module.auth.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 认证业务实现
 * <p>
 * 实现用户登录、退出登录等认证功能，基于 Sa-Token 框架
 *
 * @author 木子软件
 * @Date 2026-01-29
 * @Wechat liiayy
 * @Email 773582348@qq.com
 * @Copyright <a href="https://code.muziseo.cn">木子软件</a>
 */
@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    @Resource
    private UserService userService;

    @Resource
    private RoleService roleService;

    @Resource
    private MenuService menuService;

    @Override
    public SaTokenInfo login(LoginRequest request) {
        // 1. 校验账号密码
        UserEntity user = userService.getByUsername(request.getUsername());
        if (user == null || !user.getPassword().equals(request.getPassword())) {
            // 这里的密码比较目前是明文，实际生产应使用 BCryptPasswordEncoder
            throw new BusinessException("账号或密码错误");
        }

        // 2. 校验状态
        if (user.getStatus() != null && user.getStatus() == 1) {
            throw new BusinessException("账号已被停用");
        }

        // 3. 登录
        StpUtil.login(user.getId());

        // 4. 返回 Token
        return StpUtil.getTokenInfo();
    }

    @Override
    public void logout() {
        StpUtil.logout();
    }
}

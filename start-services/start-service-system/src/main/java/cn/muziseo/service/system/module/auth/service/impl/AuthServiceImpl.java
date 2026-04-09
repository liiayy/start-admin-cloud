package cn.muziseo.service.system.module.auth.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.muziseo.common.core.exception.BusinessException;
import cn.muziseo.common.satoken.core.util.PasswordUtils;
import cn.muziseo.service.system.enums.UserErrorCode;
import cn.muziseo.service.system.module.auth.controller.request.LoginRequest;
import cn.muziseo.service.system.module.auth.controller.vo.LoginVO;
import cn.muziseo.service.system.module.auth.repository.entity.UserEntity;
import cn.muziseo.service.system.module.auth.service.AuthService;
import cn.muziseo.service.system.module.auth.service.SaSessionRefreshService;
import cn.muziseo.service.system.module.auth.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 认证业务实现
 *
 * @author 木子软件
 */
@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    private static final String LOGIN_FAIL_KEY = "login_fail:";
    private static final int MAX_FAIL_COUNT = 5;
    private static final int LOCK_MINUTES = 15;

    @Resource
    private UserService userService;

    @Resource
    private SaSessionRefreshService saSessionRefreshService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public LoginVO login(LoginRequest request) {
        String username = request.getUsername();
        String failKey = LOGIN_FAIL_KEY + username;

        // 1. 检查是否被锁定
        String failCountStr = stringRedisTemplate.opsForValue().get(failKey);
        if (failCountStr != null && Integer.parseInt(failCountStr) >= MAX_FAIL_COUNT) {
            log.warn("账号登录被锁定: username={}", username);
            throw new BusinessException(UserErrorCode.LOGIN_FAILED,
                    "登录失败次数过多，请" + LOCK_MINUTES + "分钟后再试");
        }

        // 2. 校验账号密码
        UserEntity user = userService.getByUsername(username);
        if (user == null || !PasswordUtils.matches(request.getPassword(), user.getPassword())) {
            // 记录失败次数
            incrementFailCount(failKey);
            throw new BusinessException(UserErrorCode.LOGIN_FAILED);
        }

        // 3. 校验状态
        if (user.getStatus() != null && user.getStatus() == 1) {
            throw new BusinessException(UserErrorCode.USER_DISABLED);
        }

        // 4. 登录成功，清除失败计数
        stringRedisTemplate.delete(failKey);

        // 5. 执行登录
        StpUtil.login(user.getId());
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();

        // 6. 将角色和权限写入 Session
        saSessionRefreshService.refreshUserSession(user.getId());
        log.info("用户登录成功: userId={}, username={}", user.getId(), user.getUsername());

        // 7. 返回 Token
        return LoginVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .tokenName(tokenInfo.getTokenName())
                .tokenValue(tokenInfo.getTokenValue())
                .build();
    }

    @Override
    public void logout() {
        StpUtil.logout();
    }

    /**
     * 递增登录失败次数
     */
    private void incrementFailCount(String key) {
        Long count = stringRedisTemplate.opsForValue().increment(key);
        if (count != null && count == 1) {
            // 第一次失败，设置过期时间
            stringRedisTemplate.expire(key, LOCK_MINUTES, TimeUnit.MINUTES);
        }
    }
}

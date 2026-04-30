package cn.muziseo.service.system.module.auth.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.muziseo.common.cache.utils.RedisUtils;
import cn.muziseo.common.core.constant.CacheConstants;
import cn.muziseo.common.core.constant.ConfigConstants;
import cn.muziseo.common.core.exception.BusinessException;
import cn.muziseo.common.cache.config.ConfigUtils;
import cn.muziseo.common.satoken.core.util.PasswordUtils;
import cn.muziseo.common.core.utils.servlet.ServletUtils;
import cn.muziseo.common.core.utils.spring.SpringUtils;
import cn.muziseo.common.core.event.LoginLogEvent;
import cn.muziseo.common.log.utils.IpLocationUtils;
import cn.muziseo.service.system.enums.UserErrorCode;
import cn.muziseo.service.system.module.auth.controller.request.LoginRequest;
import cn.muziseo.service.system.module.auth.controller.vo.LoginVO;
import cn.muziseo.service.system.module.auth.repository.entity.UserEntity;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
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

    private static final int MAX_FAIL_COUNT = 5;
    private static final int LOCK_MINUTES = 15;

    @Resource
    private UserService userService;

    @Resource
    private SaSessionRefreshService saSessionRefreshService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private IpLocationUtils ipLocationUtils;

    /**
     * 用户登录认证
     * <p>
     * 1. 校验验证码（若开启）
     * 2. 检查账号是否因多次失败被锁定
     * 3. 校验账号密码
     * 4. 登录成功后加载权限数据并记录日志
     *
     * @param request 登录请求参数
     * @return 登录成功后的用户信息与 Token
     */
    @Override
    public LoginVO login(LoginRequest request) {
        // 0. 校验验证码
        validateCaptcha(request);

        String username = request.getUsername();
        String failKey = CacheConstants.LOGIN_FAIL_PREFIX + username;

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
            // 记录登录失败日志
            publishLoginLog(username, 1, "账号或密码错误");
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

        // 记录登录成功日志
        publishLoginLog(username, 0, "登录成功");

        // 7. 返回 Token
        return LoginVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .email(user.getEmail())
                .tokenName(tokenInfo.getTokenName())
                .tokenValue(tokenInfo.getTokenValue())
                .build();
    }

    /**
     * 用户退出登录
     * <p>
     * 清除 Sa-Token 会话并记录退出日志
     */
    @Override
    public void logout() {
        String username = "";
        try {
            if (StpUtil.isLogin()) {
                username = StpUtil.getLoginIdAsString();
            }
        } catch (Exception e) {
            // 忽略非登录态
        }
        StpUtil.logout();
        if (!username.isEmpty()) {
            publishLoginLog(username, 0, "退出成功");
        }
    }

    /**
     * 校验验证码
     */
    private void validateCaptcha(LoginRequest request) {
        boolean captchaEnabled = ConfigUtils.getBoolean(ConfigConstants.CAPTCHA_ENABLED, true);
        if (!captchaEnabled) {
            return;
        }

        String code = request.getCode();
        String uuid = request.getUuid();

        if (code == null || code.isEmpty()) {
            throw new BusinessException(UserErrorCode.CAPTCHA_EMPTY);
        }
        if (uuid == null || uuid.isEmpty()) {
            throw new BusinessException(UserErrorCode.CAPTCHA_EXPIRED);
        }

        String redisKey = CacheConstants.CAPTCHA_CODE_PREFIX + uuid;
        String captchaCode = RedisUtils.getCacheObject(redisKey);
        // 验证后立即删除，防止重放攻击
        RedisUtils.deleteObject(redisKey);

        if (captchaCode == null) {
            throw new BusinessException(UserErrorCode.CAPTCHA_EXPIRED);
        }
        if (!captchaCode.equalsIgnoreCase(code)) {
            throw new BusinessException(UserErrorCode.CAPTCHA_ERROR);
        }
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

    /**
     * 发布登录日志记录
     */
    private void publishLoginLog(String username, Integer status, String message) {
        HttpServletRequest request = ServletUtils.getRequest();
        if (request == null) return;

        UserAgent userAgent = UserAgentUtil.parse(request.getHeader("User-Agent"));
        
        LoginLogEvent loginLogEvent = new LoginLogEvent();
        loginLogEvent.setUsername(username);
        loginLogEvent.setStatus(status);
        loginLogEvent.setMsg(message);
        loginLogEvent.setLoginIp(ServletUtils.getClientIP(request));
        loginLogEvent.setLoginLocation(ipLocationUtils.getLocation(loginLogEvent.getLoginIp()));
        loginLogEvent.setBrowser(userAgent.getBrowser().getName() + " " + userAgent.getVersion());
        loginLogEvent.setOs(userAgent.getOs().getName());
        loginLogEvent.setCreateTime(LocalDateTime.now());
        
        SpringUtils.getApplicationContext().publishEvent(loginLogEvent);
    }
}

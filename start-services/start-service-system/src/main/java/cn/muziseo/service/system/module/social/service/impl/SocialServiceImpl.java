package cn.muziseo.service.system.module.social.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.muziseo.common.core.exception.BusinessException;
import cn.muziseo.common.core.utils.json.JsonUtils;
import cn.muziseo.common.social.config.properties.SocialProperties;
import cn.muziseo.common.social.utils.SocialUtils;
import cn.muziseo.service.system.enums.UserErrorCode;
import cn.muziseo.service.system.module.auth.controller.vo.LoginVO;
import cn.muziseo.service.system.module.auth.manager.UserManager;
import cn.muziseo.service.system.module.auth.repository.entity.UserEntity;
import cn.muziseo.service.system.module.auth.service.SaSessionRefreshService;
import cn.muziseo.service.system.module.social.manager.SocialUserManager;
import cn.muziseo.service.system.module.social.repository.entity.SocialUserEntity;
import cn.muziseo.service.system.module.social.service.SocialService;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthRequest;
import me.zhyd.oauth.utils.AuthStateUtils;
import me.zhyd.oauth.utils.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;

/**
 * 社交登录业务实现
 *
 * @author 木子软件
 */
@Service
public class SocialServiceImpl implements SocialService {

    @Resource
    private SocialProperties socialProperties;

    @Resource
    private SocialUserManager socialUserManager;

    @Resource
    private UserManager userManager;

    @Resource
    private SaSessionRefreshService saSessionRefreshService;

    @Override
    public String getAuthorizeUrl(String source) {
        AuthRequest authRequest = SocialUtils.getAuthRequest(source, socialProperties);
        return authRequest.authorize(AuthStateUtils.createState());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginVO callback(String source, String code, String state) {
        AuthResponse<AuthUser> response = getAuthResponse(source, code, state);
        AuthUser authUser = response.getData();

        SocialUserEntity socialUser = socialUserManager.getBySourceAndUuid(source, authUser.getUuid());

        // 1. 如果从未授权过，先保存社交用户信息
        if (socialUser == null) {
            socialUser = createSocialUser(authUser, source);
            socialUserManager.save(socialUser);
        } else {
            // 更新令牌信息
            updateSocialUserToken(socialUser, authUser);
            socialUserManager.updateById(socialUser);
        }

        // 2. 检查是否绑定了系统用户
        if (socialUser.getUserId() == null) {
            // 这里可以抛出异常，供前端跳转绑定页面，或者根据需求自动注册
            throw new BusinessException(UserErrorCode.SOCIAL_USER_NOT_BOUND, socialUser.getUuid());
        }

        // 3. 执行系统登录
        UserEntity user = userManager.getById(socialUser.getUserId());
        if (user == null) {
            throw new BusinessException(UserErrorCode.USER_NOT_EXISTS);
        }
        if (user.getStatus() != null && user.getStatus() == 1) {
            throw new BusinessException(UserErrorCode.USER_DISABLED);
        }

        StpUtil.login(user.getId());
        saSessionRefreshService.refreshUserSession(user.getId());

        return LoginVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .email(user.getEmail())
                .tokenName(StpUtil.getTokenName())
                .tokenValue(StpUtil.getTokenValue())
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bind(String source, String code, String state) {
        AuthResponse<AuthUser> response = getAuthResponse(source, code, state);
        AuthUser authUser = response.getData();
        Long currentUserId = StpUtil.getLoginIdAsLong();

        SocialUserEntity socialUser = socialUserManager.getBySourceAndUuid(source, authUser.getUuid());

        if (socialUser != null && socialUser.getUserId() != null) {
            if (socialUser.getUserId().equals(currentUserId)) {
                return; // 已经绑定过当前用户
            }
            throw new BusinessException(UserErrorCode.SOCIAL_USER_ALREADY_BOUND);
        }

        if (socialUser == null) {
            socialUser = createSocialUser(authUser, source);
            socialUser.setUserId(currentUserId);
            socialUserManager.save(socialUser);
        } else {
            socialUser.setUserId(currentUserId);
            updateSocialUserToken(socialUser, authUser);
            socialUserManager.updateById(socialUser);
        }
    }

    @Override
    public void unbind(String source) {
        Long currentUserId = StpUtil.getLoginIdAsLong();
        java.util.List<SocialUserEntity> list = socialUserManager.listByUserId(currentUserId);
        
        SocialUserEntity target = list.stream()
                .filter(s -> s.getSource().equalsIgnoreCase(source))
                .findFirst()
                .orElseThrow(() -> new BusinessException("未找到该平台的绑定关系"));
        
        socialUserManager.removeById(target.getId());
    }

    private AuthResponse<AuthUser> getAuthResponse(String source, String code, String state) {
        AuthRequest authRequest = SocialUtils.getAuthRequest(source, socialProperties);
        AuthCallback callback = AuthCallback.builder().code(code).state(state).build();
        AuthResponse<AuthUser> response = authRequest.login(callback);
        if (!response.ok()) {
            throw new BusinessException(UserErrorCode.LOGIN_FAILED, response.getMsg());
        }
        return response;
    }

    private SocialUserEntity createSocialUser(AuthUser authUser, String source) {
        return SocialUserEntity.builder()
                .source(source)
                .uuid(authUser.getUuid())
                .username(authUser.getUsername())
                .nickname(authUser.getNickname())
                .avatar(authUser.getAvatar())
                .accessToken(authUser.getToken().getAccessToken())
                .refreshToken(authUser.getToken().getRefreshToken())
                .rawUserInfo(JsonUtils.toJsonString(authUser.getRawUserInfo()))
                .build();
    }

    private void updateSocialUserToken(SocialUserEntity entity, AuthUser authUser) {
        entity.setAccessToken(authUser.getToken().getAccessToken());
        if (StringUtils.isNotEmpty(authUser.getToken().getRefreshToken())) {
            entity.setRefreshToken(authUser.getToken().getRefreshToken());
        }
    }
}

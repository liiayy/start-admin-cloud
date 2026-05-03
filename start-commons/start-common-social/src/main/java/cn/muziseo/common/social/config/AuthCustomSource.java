package cn.muziseo.common.social.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import me.zhyd.oauth.config.AuthSource;
import me.zhyd.oauth.request.AuthDefaultRequest;

/**
 * 自定义社交登录源实现
 * 用于支持动态 ServerUrl 的平台（如 MaxKey, TopIAM, Gitea）
 *
 * @author 木子软件
 */
@Getter
@Builder
@AllArgsConstructor
public class AuthCustomSource implements AuthSource {

    private final String name;
    private final String authorize;
    private final String accessToken;
    private final String userInfo;
    private final Class<? extends AuthDefaultRequest> targetClass;

    @Override
    public String authorize() {
        return authorize;
    }

    @Override
    public String accessToken() {
        return accessToken;
    }

    @Override
    public String userInfo() {
        return userInfo;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public Class<? extends AuthDefaultRequest> getTargetClass() {
        return targetClass;
    }
}

package cn.muziseo.common.social.gitea;

import cn.hutool.core.lang.Dict;
import cn.muziseo.common.core.utils.json.JsonUtils;
import me.zhyd.oauth.cache.AuthStateCache;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.config.AuthSource;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthToken;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthDefaultRequest;

/**
 * Gitea 认证请求实现
 *
 * @author 木子软件
 */
public class AuthGiteaRequest extends AuthDefaultRequest {

    public AuthGiteaRequest(AuthConfig config, AuthSource source, AuthStateCache authStateCache) {
        super(config, source, authStateCache);
    }

    @Override
    public AuthToken getAccessToken(AuthCallback authCallback) {
        String body = doPostAuthorizationCode(authCallback.getCode());
        Dict object = JsonUtils.parseObject(body, Dict.class);

        return AuthToken.builder()
            .accessToken(object.getStr("access_token"))
            .refreshToken(object.getStr("refresh_token"))
            .tokenType(object.getStr("token_type"))
            .build();
    }

    @Override
    public AuthUser getUserInfo(AuthToken authToken) {
        String body = doGetUserInfo(authToken);
        Dict object = JsonUtils.parseObject(body, Dict.class);

        return AuthUser.builder()
            .uuid(object.getStr("id"))
            .username(object.getStr("login"))
            .nickname(object.getStr("full_name"))
            .avatar(object.getStr("avatar_url"))
            .email(object.getStr("email"))
            .token(authToken)
            .source(source.toString())
            .build();
    }
}

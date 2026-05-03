package cn.muziseo.common.social.topiam;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.muziseo.common.core.utils.json.JsonUtils;
import me.zhyd.oauth.cache.AuthStateCache;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.config.AuthSource;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthToken;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthDefaultRequest;
import me.zhyd.oauth.utils.UrlBuilder;

/**
 * TopIAM 认证请求实现
 *
 * @author 木子软件
 */
public class AuthTopIamRequest extends AuthDefaultRequest {

    public AuthTopIamRequest(AuthConfig config, AuthSource source, AuthStateCache authStateCache) {
        super(config, source, authStateCache);
    }

    /**
     * 使用 Basic 认证方式发送客户端凭证
     */
    @Override
    public String doPostAuthorizationCode(String code) {
        HttpRequest request = HttpRequest.post(source.accessToken())
            .header("Authorization", "Basic " + Base64.encode(
                "%s:%s".formatted(config.getClientId(), config.getClientSecret())))
            .form("grant_type", "authorization_code")
            .form("code", code)
            .form("redirect_uri", config.getRedirectUri());
        HttpResponse response = request.execute();
        return response.body();
    }

    /**
     * 构建授权 URL，添加 scope 参数
     */
    @Override
    public String authorize(String state) {
        return UrlBuilder.fromBaseUrl(super.authorize(state))
            .queryParam("scope", StrUtil.join("%20", config.getScopes()))
            .build();
    }

    @Override
    public AuthToken getAccessToken(AuthCallback authCallback) {
        String body = doPostAuthorizationCode(authCallback.getCode());
        Dict object = JsonUtils.parseObject(body, Dict.class);
        return AuthToken.builder()
            .accessToken(object.getStr("access_token"))
            .refreshToken(object.getStr("refresh_token"))
            .idToken(object.getStr("id_token"))
            .tokenType(object.getStr("token_type"))
            .scope(object.getStr("scope"))
            .build();
    }

    @Override
    public AuthUser getUserInfo(AuthToken authToken) {
        String body = doGetUserInfo(authToken);
        Dict object = JsonUtils.parseObject(body, Dict.class);
        return AuthUser.builder()
            .uuid(object.getStr("sub"))
            .username(object.getStr("preferred_username"))
            .nickname(object.getStr("nickname"))
            .avatar(object.getStr("picture"))
            .email(object.getStr("email"))
            .token(authToken)
            .source(source.toString())
            .build();
    }
}

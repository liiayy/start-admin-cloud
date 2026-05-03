package cn.muziseo.common.social.utils;

import cn.hutool.core.util.ObjectUtil;
import cn.muziseo.common.core.utils.spring.SpringUtils;
import cn.muziseo.common.social.config.properties.SocialLoginConfigProperties;
import cn.muziseo.common.social.config.properties.SocialProperties;
import cn.muziseo.common.social.config.AuthCustomSource;
import cn.muziseo.common.social.gitea.AuthGiteaRequest;

import cn.muziseo.common.social.maxkey.AuthMaxKeyRequest;
import cn.muziseo.common.social.topiam.AuthTopIamRequest;
import me.zhyd.oauth.cache.AuthStateCache;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.config.AuthDefaultSource;
import me.zhyd.oauth.exception.AuthException;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.*;

/**
 * 社交登录核心工具类
 *
 * @author 木子软件
 */
public class SocialUtils {

    private static final AuthStateCache STATE_CACHE = SpringUtils.getBean(AuthStateCache.class);

    /**
     * 执行第三方登录认证
     *
     * @param source           登录平台类型
     * @param code             授权码
     * @param state            状态参数
     * @param socialProperties 社交登录配置
     * @return 认证响应结果
     */
    public static AuthResponse<AuthUser> loginAuth(
            String source, String code, String state,
            SocialProperties socialProperties) throws AuthException {
        AuthRequest authRequest = getAuthRequest(source, socialProperties);
        AuthCallback callback = new AuthCallback();
        callback.setCode(code);
        callback.setState(state);
        return authRequest.login(callback);
    }

    /**
     * 根据平台类型获取对应的认证请求对象
     */
    public static AuthRequest getAuthRequest(
            String source, SocialProperties socialProperties) throws AuthException {
        SocialLoginConfigProperties obj = socialProperties.getType().get(source);
        if (ObjectUtil.isNull(obj)) {
            throw new AuthException("不支持的第三方登录类型: " + source);
        }

        // 构建认证配置
        AuthConfig.AuthConfigBuilder builder = AuthConfig.builder()
            .clientId(obj.getClientId())
            .clientSecret(obj.getClientSecret())
            .redirectUri(obj.getRedirectUri())
            .alipayPublicKey(obj.getAlipayPublicKey())
            .stackOverflowKey(obj.getStackOverflowKey())
            .agentId(obj.getAgentId())
            .unionId(obj.isUnionId())
            .scopes(obj.getScopes());

        // 处理自定义平台
        String sourceLower = source.toLowerCase();
        switch (sourceLower) {
            case "maxkey" -> {
                AuthCustomSource customSource = AuthCustomSource.builder()
                    .name(source.toUpperCase())
                    .authorize(obj.getServerUrl() + "/sign/authz/oauth/v20/authorize")
                    .accessToken(obj.getServerUrl() + "/sign/authz/oauth/v20/token")
                    .userInfo(obj.getServerUrl() + "/sign/api/oauth/v20/me")
                    .targetClass(AuthMaxKeyRequest.class)
                    .build();
                return new AuthMaxKeyRequest(builder.build(), customSource, STATE_CACHE);
            }
            case "topiam" -> {
                AuthCustomSource customSource = AuthCustomSource.builder()
                    .name(source.toUpperCase())
                    .authorize(obj.getServerUrl() + "/oauth2/auth")
                    .accessToken(obj.getServerUrl() + "/oauth2/token")
                    .userInfo(obj.getServerUrl() + "/oauth2/userinfo")
                    .targetClass(AuthTopIamRequest.class)
                    .build();
                return new AuthTopIamRequest(builder.build(), customSource, STATE_CACHE);
            }
            case "gitea" -> {
                AuthCustomSource customSource = AuthCustomSource.builder()
                    .name(source.toUpperCase())
                    .authorize(obj.getServerUrl() + "/login/oauth/authorize")
                    .accessToken(obj.getServerUrl() + "/login/oauth/access_token")
                    .userInfo(obj.getServerUrl() + "/api/v1/user")
                    .targetClass(AuthGiteaRequest.class)
                    .build();
                return new AuthGiteaRequest(builder.build(), customSource, STATE_CACHE);
            }
            default -> {

                // 处理标准平台 (JustAuth 原生支持)
                try {
                    AuthDefaultSource defaultSource = AuthDefaultSource.valueOf(source.toUpperCase());
                    return getStandardAuthRequest(defaultSource, builder.build());
                } catch (IllegalArgumentException e) {
                    throw new AuthException("未定义的社交登录平台: " + source);
                }
            }
        }
    }

    /**
     * 获取 JustAuth 原生支持的平台请求对象
     */
    private static AuthRequest getStandardAuthRequest(AuthDefaultSource source, AuthConfig config) {
        return switch (source) {
            case GITHUB -> new AuthGithubRequest(config, STATE_CACHE);
            case GITEE -> new AuthGiteeRequest(config, STATE_CACHE);
            case DINGTALK -> new AuthDingTalkV2Request(config, STATE_CACHE);
            case BAIDU -> new AuthBaiduRequest(config, STATE_CACHE);
            case WEIBO -> new AuthWeiboRequest(config, STATE_CACHE);
            case CODING -> new AuthCodingRequest(config, STATE_CACHE);
            case OSCHINA -> new AuthOschinaRequest(config, STATE_CACHE);
            case ALIPAY -> new AuthAlipayRequest(config, STATE_CACHE);
            case QQ -> new AuthQqRequest(config, STATE_CACHE);
            case WECHAT_OPEN -> new AuthWeChatOpenRequest(config, STATE_CACHE);
            case TAOBAO -> new AuthTaobaoRequest(config, STATE_CACHE);
            case DOUYIN -> new AuthDouyinRequest(config, STATE_CACHE);
            case LINKEDIN -> new AuthLinkedinRequest(config, STATE_CACHE);
            case MICROSOFT -> new AuthMicrosoftRequest(config, STATE_CACHE);
            case RENREN -> new AuthRenrenRequest(config, STATE_CACHE);
            case STACK_OVERFLOW -> new AuthStackOverflowRequest(config, STATE_CACHE);
            case HUAWEI -> new AuthHuaweiV3Request(config, STATE_CACHE);
            case WECHAT_ENTERPRISE -> new AuthWeChatEnterpriseQrcodeV2Request(config, STATE_CACHE);
            case GITLAB -> new AuthGitlabRequest(config, STATE_CACHE);
            case WECHAT_MP -> new AuthWeChatMpRequest(config, STATE_CACHE);
            case ALIYUN -> new AuthAliyunRequest(config, STATE_CACHE);
            default -> throw new AuthException("暂不支持该原生平台: " + source);
        };
    }
}


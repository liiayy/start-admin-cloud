package cn.muziseo.service.system.module.auth.service;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.muziseo.service.system.module.auth.controller.request.LoginRequest;

/**
 * 认证业务接口
 */
public interface AuthService {

    /**
     * 登录
     *
     * @param request 登录请求
     * @return Token信息
     */
    SaTokenInfo login(LoginRequest request);

    /**
     * 退出登录
     */
    void logout();

}

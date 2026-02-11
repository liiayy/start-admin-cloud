package cn.muziseo.service.system.module.auth.service;

import cn.muziseo.service.system.module.auth.controller.request.LoginRequest;
import cn.muziseo.service.system.module.auth.controller.vo.LoginVO;

/**
 * 认证业务接口
 * <p>
 * 提供用户登录、退出登录等认证相关功能
 *
 * @author 木子软件
 * @Date 2026-01-29
 * @Wechat liiayy
 * @Email 773582348@qq.com
 * @Copyright <a href="https://code.muziseo.cn">木子软件</a>
 */
public interface AuthService {

    /**
     * 登录
     *
     * @param request 登录请求
     * @return Token信息
     */
    LoginVO login(LoginRequest request);

    /**
     * 退出登录
     */
    void logout();

}

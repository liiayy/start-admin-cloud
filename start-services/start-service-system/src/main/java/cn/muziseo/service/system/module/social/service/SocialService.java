package cn.muziseo.service.system.module.social.service;

import cn.muziseo.service.system.module.auth.controller.vo.LoginVO;

/**
 * 社交登录业务接口
 *
 * @author 木子软件
 */
public interface SocialService {

    /**
     * 获取第三方登录授权地址
     *
     * @param source 平台类型
     * @return 授权跳转地址
     */
    String getAuthorizeUrl(String source);

    /**
     * 社交登录回调处理
     *
     * @param source 平台类型
     * @param code   授权码
     * @param state  状态参数
     * @return 登录成功后的用户信息；若未绑定，则抛出异常或返回特定标识供前端跳转绑定页面
     */
    LoginVO callback(String source, String code, String state);

    /**
     * 绑定社交账号（针对已登录用户）
     *
     * @param source 平台类型
     * @param code   授权码
     * @param state  状态参数
     */
    void bind(String source, String code, String state);

    /**
     * 解绑社交账号
     *
     * @param source 平台类型
     */
    void unbind(String source);
}

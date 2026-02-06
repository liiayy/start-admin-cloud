package cn.muziseo.service.system.module.auth.service;

import cn.muziseo.service.system.module.auth.repository.entity.UserEntity;

/**
 * 用户业务接口
 * <p>
 * 提供用户的增删改查、用户名查询等功能
 *
 * @author 木子软件
 * @Date 2026-01-29
 * @Wechat liiayy
 * @Email 773582348@qq.com
 * @Copyright <a href="https://code.muziseo.cn">木子软件</a>
 */
public interface UserService {
    /**
     * 根据用户名获取用户
     *
     * @param username 用户名
     * @return 用户实体
     */
    UserEntity getByUsername(String username);

    /**
     * 添加用户
     *
     * @param request 添加请求
     */
    void addUser(cn.muziseo.service.system.module.auth.controller.request.UserAddRequest request);

    /**
     * 根据ID获取用户
     *
     * @param id 用户ID
     * @return 用户实体
     */
    UserEntity getUserById(Long id);
}

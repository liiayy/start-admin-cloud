package cn.muziseo.service.system.module.auth.service;

import cn.muziseo.service.system.module.auth.repository.entity.UserEntity;

/**
 * 用户业务接口
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

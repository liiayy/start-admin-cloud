package cn.muziseo.service.system.module.auth.service;

import cn.muziseo.common.db.page.PageResponse;
import cn.muziseo.service.system.module.auth.controller.request.*;
import cn.muziseo.service.system.module.auth.controller.vo.UserDetailVO;
import cn.muziseo.service.system.module.auth.controller.vo.UserVO;
import cn.muziseo.service.system.module.auth.repository.entity.UserEntity;
import cn.muziseo.service.system.module.auth.controller.vo.UserImportVO;

import java.util.List;

/**
 * 用户业务接口
 *
 * @author 木子软件
 */
public interface UserService {

    UserEntity getByUsername(String username);

    UserEntity getUserById(Long id);

    PageResponse<UserVO> pageUser(UserPageRequest request);

    List<UserVO> listUser(UserPageRequest request);

    UserVO getUser(Long id);

    void createUser(UserCreateRequest request);

    void updateUser(UserUpdateRequest request);

    void deleteUser(Long id);

    void updateStatus(UserUpdateStatusRequest request);

    void resetPassword(UserResetPasswordRequest request);

    void updatePassword(UserUpdatePasswordRequest request);

    void assignRole(UserRoleAssignRequest request);

    String importUsers(List<UserImportVO> list, boolean updateSupport);
}

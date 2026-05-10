package cn.muziseo.service.system.module.auth.service.impl;

import cn.muziseo.common.core.exception.BusinessException;
import cn.muziseo.service.system.enums.UserErrorCode;
import cn.muziseo.service.system.module.auth.controller.request.*;
import cn.muziseo.service.system.module.auth.controller.vo.UserVO;
import cn.muziseo.service.system.module.auth.manager.UserManager;
import cn.muziseo.service.system.module.auth.manager.UserRoleManager;
import cn.muziseo.service.system.module.auth.repository.entity.UserEntity;
import cn.muziseo.service.system.module.auth.service.SaSessionRefreshService;
import cn.muziseo.service.system.module.permission.service.MenuService;
import cn.muziseo.service.system.module.permission.service.RoleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

/**
 * UserServiceImpl 单元测试
 * <p>
 * 聚焦 Service 层业务逻辑，Mock Manager 层隔离数据访问
 *
 * @author 木子软件
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserManager userManager;

    @Mock
    private UserRoleManager userRoleManager;

    @Mock
    private RoleService roleService;

    @Mock
    private MenuService menuService;

    @Mock
    private SaSessionRefreshService saSessionRefreshService;

    @InjectMocks
    private UserServiceImpl userService;

    // ==================== getUser ====================

    @Nested
    @DisplayName("getUser - 查询用户详情")
    class GetUserTests {

        @Test
        @DisplayName("用户存在时返回 UserVO")
        void getUser_existingId_returnsUserVO() {
            // Given
            UserEntity entity = buildUserEntity(1L, "admin", "管理员");
            when(userManager.getById(1L)).thenReturn(entity);

            // When
            UserVO result = userService.getUser(1L);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getUsername()).isEqualTo("admin");
            assertThat(result.getNickname()).isEqualTo("管理员");
        }

        @Test
        @DisplayName("用户不存在时抛出 BusinessException")
        void getUser_nonexistentId_throwsBusinessException() {
            // Given
            when(userManager.getById(999L)).thenReturn(null);

            // When & Then
            assertThatThrownBy(() -> userService.getUser(999L))
                    .isInstanceOf(BusinessException.class);
        }
    }

    // ==================== getByUsername ====================

    @Nested
    @DisplayName("getByUsername - 根据用户名查询")
    class GetByUsernameTests {

        @Test
        @DisplayName("用户名存在时返回实体")
        void getByUsername_existingUsername_returnsEntity() {
            UserEntity entity = buildUserEntity(1L, "admin", "管理员");
            when(userManager.getByUsername("admin")).thenReturn(entity);

            UserEntity result = userService.getByUsername("admin");

            assertThat(result).isNotNull();
            assertThat(result.getUsername()).isEqualTo("admin");
        }

        @Test
        @DisplayName("用户名不存在时返回 null")
        void getByUsername_nonexistentUsername_returnsNull() {
            when(userManager.getByUsername("notexist")).thenReturn(null);

            UserEntity result = userService.getByUsername("notexist");

            assertThat(result).isNull();
        }
    }

    // ==================== getUserById ====================

    @Nested
    @DisplayName("getUserById - 根据ID查询实体")
    class GetUserByIdTests {

        @Test
        @DisplayName("ID 存在时返回实体")
        void getUserById_existingId_returnsEntity() {
            UserEntity entity = buildUserEntity(1L, "admin", "管理员");
            when(userManager.getById(1L)).thenReturn(entity);

            UserEntity result = userService.getUserById(1L);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("ID 不存在时返回 null")
        void getUserById_nonexistentId_returnsNull() {
            when(userManager.getById(999L)).thenReturn(null);

            UserEntity result = userService.getUserById(999L);

            assertThat(result).isNull();
        }
    }

    // ==================== createUser ====================

    @Nested
    @DisplayName("createUser - 创建用户")
    class CreateUserTests {

        @Test
        @DisplayName("用户名已存在时抛出异常")
        void createUser_usernameExists_throwsBusinessException() {
            // Given
            UserCreateRequest request = new UserCreateRequest();
            request.setUsername("admin");
            request.setPassword("123456");
            request.setNickname("管理员");
            when(userManager.existsByUsername(eq("admin"), any())).thenReturn(true);

            // When & Then
            assertThatThrownBy(() -> userService.createUser(request))
                    .isInstanceOf(BusinessException.class);

            verify(userManager, never()).save(any());
        }

        @Test
        @DisplayName("正常创建用户，密码被加密")
        void createUser_validRequest_savesWithEncodedPassword() {
            // Given
            UserCreateRequest request = new UserCreateRequest();
            request.setUsername("newuser");
            request.setPassword("123456");
            request.setNickname("新用户");
            when(userManager.existsByUsername(eq("newuser"), any())).thenReturn(false);
            when(userManager.save(any(UserEntity.class))).thenReturn(true);

            // When
            userService.createUser(request);

            // Then
            ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
            verify(userManager).save(captor.capture());

            UserEntity saved = captor.getValue();
            assertThat(saved.getUsername()).isEqualTo("newuser");
            assertThat(saved.getPassword()).isNotEqualTo("123456"); // 密码已加密
            assertThat(saved.getStatus()).isEqualTo(0); // 默认状态
        }

        @Test
        @DisplayName("创建用户时指定了状态，不使用默认值")
        void createUser_withStatus_usesProvidedStatus() {
            // Given
            UserCreateRequest request = new UserCreateRequest();
            request.setUsername("newuser");
            request.setPassword("123456");
            request.setNickname("新用户");
            request.setStatus(1); // 指定停用状态
            when(userManager.existsByUsername(eq("newuser"), any())).thenReturn(false);
            when(userManager.save(any(UserEntity.class))).thenReturn(true);

            // When
            userService.createUser(request);

            // Then
            ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
            verify(userManager).save(captor.capture());
            assertThat(captor.getValue().getStatus()).isEqualTo(1);
        }
    }

    // ==================== updateUser ====================

    @Nested
    @DisplayName("updateUser - 更新用户")
    class UpdateUserTests {

        @Test
        @DisplayName("用户不存在时抛出异常")
        void updateUser_nonexistentUser_throwsBusinessException() {
            // Given
            UserUpdateRequest request = new UserUpdateRequest();
            request.setId(999L);
            when(userManager.getById(999L)).thenReturn(null);

            // When & Then
            assertThatThrownBy(() -> userService.updateUser(request))
                    .isInstanceOf(BusinessException.class);

            verify(userManager, never()).updateById(any());
        }

        @Test
        @DisplayName("正常更新用户信息")
        void updateUser_validRequest_updatesSuccessfully() {
            // Given
            UserEntity existing = buildUserEntity(1L, "admin", "旧昵称");
            when(userManager.getById(1L)).thenReturn(existing);
            when(userManager.updateById(any(UserEntity.class))).thenReturn(true);

            UserUpdateRequest request = new UserUpdateRequest();
            request.setId(1L);
            request.setNickname("新昵称");
            request.setEmail("new@example.com");

            // When
            userService.updateUser(request);

            // Then
            ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
            verify(userManager).updateById(captor.capture());

            UserEntity updated = captor.getValue();
            assertThat(updated.getId()).isEqualTo(1L);
            assertThat(updated.getNickname()).isEqualTo("新昵称");
            assertThat(updated.getEmail()).isEqualTo("new@example.com");
        }
    }

    // ==================== deleteUser ====================

    @Nested
    @DisplayName("deleteUser - 删除用户")
    class DeleteUserTests {

        @Test
        @DisplayName("用户不存在时抛出异常")
        void deleteUser_nonexistentUser_throwsBusinessException() {
            when(userManager.getById(999L)).thenReturn(null);

            assertThatThrownBy(() -> userService.deleteUser(999L))
                    .isInstanceOf(BusinessException.class);

            verify(userRoleManager, never()).deleteByUserId(any());
            verify(userManager, never()).removeById(any());
        }

        @Test
        @DisplayName("删除用户同时删除角色关联")
        void deleteUser_existingUser_deletesUserAndRoles() {
            // Given
            UserEntity existing = buildUserEntity(1L, "admin", "管理员");
            when(userManager.getById(1L)).thenReturn(existing);
            when(userManager.removeById(1L)).thenReturn(true);

            // When
            userService.deleteUser(1L);

            // Then：验证协调了多个 Manager
            verify(userRoleManager).deleteByUserId(1L);
            verify(userManager).removeById(1L);
        }
    }

    // ==================== updateStatus ====================

    @Nested
    @DisplayName("updateStatus - 更新用户状态")
    class UpdateStatusTests {

        @Test
        @DisplayName("用户不存在时抛出异常")
        void updateStatus_nonexistentUser_throwsBusinessException() {
            UserUpdateStatusRequest request = new UserUpdateStatusRequest();
            request.setId(999L);
            request.setStatus(1);
            when(userManager.getById(999L)).thenReturn(null);

            assertThatThrownBy(() -> userService.updateStatus(request))
                    .isInstanceOf(BusinessException.class);
        }

        @Test
        @DisplayName("正常更新状态")
        void updateStatus_validRequest_updatesStatus() {
            // Given
            UserEntity existing = buildUserEntity(1L, "admin", "管理员");
            when(userManager.getById(1L)).thenReturn(existing);
            when(userManager.updateById(any(UserEntity.class))).thenReturn(true);

            UserUpdateStatusRequest request = new UserUpdateStatusRequest();
            request.setId(1L);
            request.setStatus(1);

            // When
            userService.updateStatus(request);

            // Then
            ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
            verify(userManager).updateById(captor.capture());
            assertThat(captor.getValue().getStatus()).isEqualTo(1);
        }
    }

    // ==================== resetPassword ====================

    @Nested
    @DisplayName("resetPassword - 重置密码")
    class ResetPasswordTests {

        @Test
        @DisplayName("用户不存在时抛出异常")
        void resetPassword_nonexistentUser_throwsBusinessException() {
            UserResetPasswordRequest request = new UserResetPasswordRequest();
            request.setId(999L);
            request.setNewPassword("newpass");
            when(userManager.getById(999L)).thenReturn(null);

            assertThatThrownBy(() -> userService.resetPassword(request))
                    .isInstanceOf(BusinessException.class);
        }

        @Test
        @DisplayName("正常重置密码，密码被加密")
        void resetPassword_validRequest_resetsWithEncodedPassword() {
            // Given
            UserEntity existing = buildUserEntity(1L, "admin", "管理员");
            when(userManager.getById(1L)).thenReturn(existing);
            when(userManager.updateById(any(UserEntity.class))).thenReturn(true);

            UserResetPasswordRequest request = new UserResetPasswordRequest();
            request.setId(1L);
            request.setNewPassword("newpass123");

            // When
            userService.resetPassword(request);

            // Then
            ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
            verify(userManager).updateById(captor.capture());
            assertThat(captor.getValue().getPassword()).isNotEqualTo("newpass123");
        }
    }

    // ==================== updatePassword ====================

    @Nested
    @DisplayName("updatePassword - 修改密码（用户自己）")
    class UpdatePasswordTests {

        @Test
        @DisplayName("用户不存在时抛出异常")
        void updatePassword_nonexistentUser_throwsBusinessException() {
            // Mock Sa-Token 获取当前登录用户
            try (var stpUtilMock = mockStatic(cn.dev33.satoken.stp.StpUtil.class)) {
                stpUtilMock.when(cn.dev33.satoken.stp.StpUtil::getLoginIdAsLong).thenReturn(999L);
                when(userManager.getById(999L)).thenReturn(null);

                UserUpdatePasswordRequest request = new UserUpdatePasswordRequest();
                request.setOldPassword("oldpass");
                request.setNewPassword("newpass");

                assertThatThrownBy(() -> userService.updatePassword(request))
                        .isInstanceOf(BusinessException.class);
            }
        }

        @Test
        @DisplayName("旧密码错误时抛出异常")
        void updatePassword_wrongOldPassword_throwsBusinessException() {
            try (var stpUtilMock = mockStatic(cn.dev33.satoken.stp.StpUtil.class)) {
                stpUtilMock.when(cn.dev33.satoken.stp.StpUtil::getLoginIdAsLong).thenReturn(1L);

                UserEntity existing = buildUserEntity(1L, "admin", "管理员");
                existing.setPassword("encoded_password");
                when(userManager.getById(1L)).thenReturn(existing);

                // Mock PasswordUtils
                try (var passwordUtilsMock = mockStatic(cn.muziseo.common.satoken.core.util.PasswordUtils.class)) {
                    passwordUtilsMock.when(() ->
                            cn.muziseo.common.satoken.core.util.PasswordUtils.matches("wrongpass", "encoded_password")
                    ).thenReturn(false);

                    UserUpdatePasswordRequest request = new UserUpdatePasswordRequest();
                    request.setOldPassword("wrongpass");
                    request.setNewPassword("newpass");

                    assertThatThrownBy(() -> userService.updatePassword(request))
                            .isInstanceOf(BusinessException.class);

                    verify(userManager, never()).updateById(any());
                }
            }
        }

        @Test
        @DisplayName("旧密码正确时更新密码")
        void updatePassword_correctOldPassword_updatesPassword() {
            try (var stpUtilMock = mockStatic(cn.dev33.satoken.stp.StpUtil.class)) {
                stpUtilMock.when(cn.dev33.satoken.stp.StpUtil::getLoginIdAsLong).thenReturn(1L);

                UserEntity existing = buildUserEntity(1L, "admin", "管理员");
                existing.setPassword("encoded_password");
                when(userManager.getById(1L)).thenReturn(existing);
                when(userManager.updateById(any(UserEntity.class))).thenReturn(true);

                try (var passwordUtilsMock = mockStatic(cn.muziseo.common.satoken.core.util.PasswordUtils.class)) {
                    passwordUtilsMock.when(() ->
                            cn.muziseo.common.satoken.core.util.PasswordUtils.matches("oldpass", "encoded_password")
                    ).thenReturn(true);
                    passwordUtilsMock.when(() ->
                            cn.muziseo.common.satoken.core.util.PasswordUtils.encode("newpass")
                    ).thenReturn("new_encoded_password");

                    UserUpdatePasswordRequest request = new UserUpdatePasswordRequest();
                    request.setOldPassword("oldpass");
                    request.setNewPassword("newpass");

                    userService.updatePassword(request);

                    ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
                    verify(userManager).updateById(captor.capture());
                    assertThat(captor.getValue().getPassword()).isEqualTo("new_encoded_password");
                }
            }
        }
    }

    // ==================== assignRole ====================

    @Nested
    @DisplayName("assignRole - 分配用户角色")
    class AssignRoleTests {

        @Test
        @DisplayName("分配角色时先删除旧关联再插入新关联")
        void assignRole_validRequest_deletesOldAndInsertsNew() {
            // Given
            Long userId = 1L;
            List<Long> roleIds = List.of(10L, 20L, 30L);

            UserRoleAssignRequest request = new UserRoleAssignRequest();
            request.setUserId(userId);
            request.setRoleIds(roleIds);

            // When
            userService.assignRole(request);

            // Then
            verify(userRoleManager).deleteByUserId(userId);
            verify(userRoleManager).batchInsert(userId, roleIds);
            verify(saSessionRefreshService).refreshUserSession(userId);
        }

        @Test
        @DisplayName("分配空角色列表时只删除不插入")
        void assignRole_emptyRoleIds_onlyDeletes() {
            UserRoleAssignRequest request = new UserRoleAssignRequest();
            request.setUserId(1L);
            request.setRoleIds(List.of());

            userService.assignRole(request);

            verify(userRoleManager).deleteByUserId(1L);
            verify(userRoleManager).batchInsert(1L, List.of());
            verify(saSessionRefreshService).refreshUserSession(1L);
        }
    }

    // ==================== 辅助方法 ====================

    private UserEntity buildUserEntity(Long id, String username, String nickname) {
        return UserEntity.builder()
                .id(id)
                .username(username)
                .nickname(nickname)
                .status(0)
                .build();
    }
}

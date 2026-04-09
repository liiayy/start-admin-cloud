package cn.muziseo.service.system.module.auth.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.muziseo.common.core.exception.BusinessException;
import cn.muziseo.service.system.enums.UserErrorCode;
import cn.muziseo.service.system.module.auth.controller.request.LoginRequest;
import cn.muziseo.service.system.module.auth.controller.vo.LoginVO;
import cn.muziseo.service.system.module.auth.repository.entity.UserEntity;
import cn.muziseo.service.system.module.auth.service.SaSessionRefreshService;
import cn.muziseo.service.system.module.auth.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * AuthServiceImpl 单元测试
 * <p>
 * 覆盖登录流程：锁定检查、密码校验、状态校验、登录成功
 *
 * @author 木子软件
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private SaSessionRefreshService saSessionRefreshService;

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private AuthServiceImpl authService;

    // ==================== login ====================

    @Nested
    @DisplayName("login - 用户登录")
    class LoginTests {

        @Test
        @DisplayName("账号被锁定时抛出异常")
        void login_accountLocked_throwsBusinessException() {
            // Given
            LoginRequest request = new LoginRequest();
            request.setUsername("admin");
            request.setPassword("123456");

            when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.get("login_fail:admin")).thenReturn("5");

            // When & Then
            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(BusinessException.class)
                    .extracting("message").asString()
                    .contains("登录失败次数过多");

            // 不应查询用户
            verify(userService, never()).getByUsername(any());
        }

        @Test
        @DisplayName("用户不存在时抛出异常并记录失败次数")
        void login_userNotFound_throwsAndIncrementsFailCount() {
            // Given
            LoginRequest request = new LoginRequest();
            request.setUsername("notexist");
            request.setPassword("123456");

            when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.get("login_fail:notexist")).thenReturn(null);
            when(userService.getByUsername("notexist")).thenReturn(null);
            when(valueOperations.increment("login_fail:notexist")).thenReturn(1L);

            // When & Then
            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(BusinessException.class);

            verify(valueOperations).increment("login_fail:notexist");
            verify(stringRedisTemplate).expire("login_fail:notexist", 15, java.util.concurrent.TimeUnit.MINUTES);
        }

        @Test
        @DisplayName("密码错误时抛出异常并记录失败次数")
        void login_wrongPassword_throwsAndIncrementsFailCount() {
            // Given
            LoginRequest request = new LoginRequest();
            request.setUsername("admin");
            request.setPassword("wrongpass");

            UserEntity user = UserEntity.builder()
                    .id(1L)
                    .username("admin")
                    .password("encoded_password")
                    .status(0)
                    .build();

            when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.get("login_fail:admin")).thenReturn(null);
            when(userService.getByUsername("admin")).thenReturn(user);
            when(valueOperations.increment("login_fail:admin")).thenReturn(1L);

            // Mock PasswordUtils.matches
            try (MockedStatic<cn.muziseo.common.satoken.core.util.PasswordUtils> passwordUtilsMock =
                         mockStatic(cn.muziseo.common.satoken.core.util.PasswordUtils.class)) {
                passwordUtilsMock.when(() ->
                        cn.muziseo.common.satoken.core.util.PasswordUtils.matches("wrongpass", "encoded_password")
                ).thenReturn(false);

                // When & Then
                assertThatThrownBy(() -> authService.login(request))
                        .isInstanceOf(BusinessException.class);

                verify(valueOperations).increment("login_fail:admin");
            }
        }

        @Test
        @DisplayName("用户被停用时抛出异常")
        void login_disabledUser_throwsBusinessException() {
            // Given
            LoginRequest request = new LoginRequest();
            request.setUsername("admin");
            request.setPassword("123456");

            UserEntity user = UserEntity.builder()
                    .id(1L)
                    .username("admin")
                    .password("encoded_password")
                    .status(1) // 停用
                    .build();

            when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.get("login_fail:admin")).thenReturn(null);
            when(userService.getByUsername("admin")).thenReturn(user);

            try (MockedStatic<cn.muziseo.common.satoken.core.util.PasswordUtils> passwordUtilsMock =
                         mockStatic(cn.muziseo.common.satoken.core.util.PasswordUtils.class)) {
                passwordUtilsMock.when(() ->
                        cn.muziseo.common.satoken.core.util.PasswordUtils.matches("123456", "encoded_password")
                ).thenReturn(true);

                // When & Then
                assertThatThrownBy(() -> authService.login(request))
                        .isInstanceOf(BusinessException.class);

                // 不应记录失败次数（密码正确，只是状态不对）
                verify(valueOperations, never()).increment(any());
            }
        }

        @Test
        @DisplayName("登录成功时清除失败计数并返回 Token")
        void login_success_returnsLoginVO() {
            // Given
            LoginRequest request = new LoginRequest();
            request.setUsername("admin");
            request.setPassword("123456");

            UserEntity user = UserEntity.builder()
                    .id(1L)
                    .username("admin")
                    .nickname("管理员")
                    .avatar("http://avatar.png")
                    .password("encoded_password")
                    .status(0)
                    .build();

            SaTokenInfo tokenInfo = new SaTokenInfo();
            tokenInfo.setTokenName("Authorization");
            tokenInfo.setTokenValue("token-value-123");

            when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.get("login_fail:admin")).thenReturn(null);
            when(userService.getByUsername("admin")).thenReturn(user);

            try (MockedStatic<cn.muziseo.common.satoken.core.util.PasswordUtils> passwordUtilsMock =
                         mockStatic(cn.muziseo.common.satoken.core.util.PasswordUtils.class);
                 MockedStatic<cn.dev33.satoken.stp.StpUtil> stpUtilMock =
                         mockStatic(cn.dev33.satoken.stp.StpUtil.class)) {

                passwordUtilsMock.when(() ->
                        cn.muziseo.common.satoken.core.util.PasswordUtils.matches("123456", "encoded_password")
                ).thenReturn(true);

                stpUtilMock.when(() -> cn.dev33.satoken.stp.StpUtil.login(1L)).then(invocation -> null);
                stpUtilMock.when(cn.dev33.satoken.stp.StpUtil::getTokenInfo).thenReturn(tokenInfo);

                // When
                LoginVO result = authService.login(request);

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getId()).isEqualTo(1L);
                assertThat(result.getUsername()).isEqualTo("admin");
                assertThat(result.getNickname()).isEqualTo("管理员");
                assertThat(result.getTokenName()).isEqualTo("Authorization");
                assertThat(result.getTokenValue()).isEqualTo("token-value-123");

                // 验证清除失败计数
                verify(stringRedisTemplate).delete("login_fail:admin");
                // 验证刷新 Session
                verify(saSessionRefreshService).refreshUserSession(1L);
            }
        }

        @Test
        @DisplayName("失败次数未达上限时允许继续尝试")
        void login_belowMaxFailCount_allowsLoginAttempt() {
            // Given
            LoginRequest request = new LoginRequest();
            request.setUsername("admin");
            request.setPassword("123456");

            UserEntity user = UserEntity.builder()
                    .id(1L)
                    .username("admin")
                    .password("encoded_password")
                    .status(0)
                    .nickname("管理员")
                    .build();

            SaTokenInfo tokenInfo = new SaTokenInfo();
            tokenInfo.setTokenName("Authorization");
            tokenInfo.setTokenValue("token-123");

            when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.get("login_fail:admin")).thenReturn("3"); // 3次失败，未达上限5次
            when(userService.getByUsername("admin")).thenReturn(user);

            try (MockedStatic<cn.muziseo.common.satoken.core.util.PasswordUtils> passwordUtilsMock =
                         mockStatic(cn.muziseo.common.satoken.core.util.PasswordUtils.class);
                 MockedStatic<cn.dev33.satoken.stp.StpUtil> stpUtilMock =
                         mockStatic(cn.dev33.satoken.stp.StpUtil.class)) {

                passwordUtilsMock.when(() ->
                        cn.muziseo.common.satoken.core.util.PasswordUtils.matches("123456", "encoded_password")
                ).thenReturn(true);

                stpUtilMock.when(() -> cn.dev33.satoken.stp.StpUtil.login(1L)).then(invocation -> null);
                stpUtilMock.when(cn.dev33.satoken.stp.StpUtil::getTokenInfo).thenReturn(tokenInfo);

                // When
                LoginVO result = authService.login(request);

                // Then - 登录成功
                assertThat(result).isNotNull();
                assertThat(result.getId()).isEqualTo(1L);
            }
        }
    }

    // ==================== logout ====================

    @Nested
    @DisplayName("logout - 用户登出")
    class LogoutTests {

        @Test
        @DisplayName("登出时调用 StpUtil.logout()")
        void logout_callsStpUtilLogout() {
            try (MockedStatic<cn.dev33.satoken.stp.StpUtil> stpUtilMock =
                         mockStatic(cn.dev33.satoken.stp.StpUtil.class)) {

                stpUtilMock.when(cn.dev33.satoken.stp.StpUtil::logout).then(invocation -> null);

                authService.logout();

                stpUtilMock.verify(cn.dev33.satoken.stp.StpUtil::logout);
            }
        }
    }
}
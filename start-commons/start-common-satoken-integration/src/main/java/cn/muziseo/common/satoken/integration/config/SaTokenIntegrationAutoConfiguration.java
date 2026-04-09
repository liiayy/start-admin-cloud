package cn.muziseo.common.satoken.integration.config;

import cn.dev33.satoken.stp.StpInterface;
import cn.muziseo.common.satoken.integration.service.SaPermissionService;
import cn.muziseo.common.satoken.integration.service.SaPermissionServiceImpl;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Set;

/**
 * Sa-Token 权限集成自动配置
 * <p>
 * 注册 StpInterface 实现，从 Sa-Token Session 中读取角色和权限。
 * 业务服务只需引入本模块 + common-satoken 即可，无需自行实现权限加载。
 *
 * @author 木子软件
 */
@AutoConfiguration
public class SaTokenIntegrationAutoConfiguration {

    @Bean
    public SaPermissionService saPermissionService() {
        return new SaPermissionServiceImpl();
    }

    @Bean
    public StpInterface stpInterface(SaPermissionService saPermissionService) {
        return new StpInterface() {

            @Override
            public List<String> getPermissionList(Object loginId, String loginType) {
                Set<String> permissions = saPermissionService.getPermissionList(
                        Long.valueOf(loginId.toString()));
                return List.copyOf(permissions);
            }

            @Override
            public List<String> getRoleList(Object loginId, String loginType) {
                Set<String> roles = saPermissionService.getRoleCodes(
                        Long.valueOf(loginId.toString()));
                return List.copyOf(roles);
            }
        };
    }
}

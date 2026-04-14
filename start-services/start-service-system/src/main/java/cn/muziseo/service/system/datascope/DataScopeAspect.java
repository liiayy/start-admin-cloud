package cn.muziseo.service.system.datascope;

import cn.dev33.satoken.stp.StpUtil;
import cn.muziseo.common.db.annotation.DataScope;
import cn.muziseo.common.db.datascope.DataScopeContext;
import cn.muziseo.common.db.datascope.DataScopeInfo;
import cn.muziseo.service.system.module.auth.manager.UserManager;
import cn.muziseo.service.system.module.auth.repository.entity.UserEntity;
import cn.muziseo.service.system.module.organization.manager.DeptManager;
import cn.muziseo.service.system.module.permission.repository.entity.RoleEntity;
import cn.muziseo.service.system.module.permission.service.RoleService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * 数据权限切面
 * <p>
 * 拦截标注了 @DataScope 的 Service 方法，解析当前用户的数据范围，
 * 将允许的部门 ID 列表放入 DataScopeContext。
 * MyBatis-Flex 的 prepareAuth 方言会自动在 SQL 中注入 dept_id 过滤条件。
 *
 * @author 木子软件
 */
@Aspect
@Component
@Slf4j
public class DataScopeAspect {

    @Resource
    private UserManager userManager;

    @Resource
    private RoleService roleService;

    @Resource
    private DeptManager deptManager;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Before("@annotation(dataScope)")
    public void doBefore(JoinPoint point, DataScope dataScope) {
        DataScopeInfo info = new DataScopeInfo();

        try {
            // 1. 获取当前用户 ID
            Long userId = StpUtil.getLoginIdAsLong();

            // 2. 获取用户实体（取 deptId）
            UserEntity user = userManager.getById(userId);
            if (user == null) {
                info.setFilter(false);
                DataScopeContext.set(info);
                return;
            }

            // 3. 获取用户角色列表，找到范围最大的（dataScope 值最小）
            List<RoleEntity> roles = roleService.getRolesByUserId(userId);
            if (roles.isEmpty()) {
                info.setFilter(false);
                DataScopeContext.set(info);
                return;
            }

            int maxScope = Integer.MAX_VALUE;
            String customDeptIds = null;
            for (RoleEntity role : roles) {
                Integer scope = role.getDataScope();
                if (scope != null && scope < maxScope) {
                    maxScope = scope;
                }
                if (scope != null && scope == 2 && customDeptIds == null) {
                    customDeptIds = role.getDataScopeDeptIds();
                }
            }

            if (maxScope == Integer.MAX_VALUE) {
                maxScope = 1;
            }

            // 4. 根据范围计算 deptIds
            switch (maxScope) {
                case 1: // 全部数据权限
                    info.setFilter(false);
                    break;
                case 2: // 自定义数据权限
                    info.setFilter(true);
                    info.setDeptIds(parseCustomDeptIds(customDeptIds));
                    break;
                case 3: // 本部门数据权限
                    info.setFilter(true);
                    info.setDeptIds(user.getDeptId() != null
                            ? List.of(user.getDeptId())
                            : Collections.emptyList());
                    break;
                case 4: // 本部门及以下数据权限
                    info.setFilter(true);
                    info.setDeptIds(deptManager.getDeptAndChildIds(user.getDeptId()));
                    break;
                default:
                    info.setFilter(false);
                    break;
            }

            log.debug("数据权限过滤: userId={}, scope={}, filter={}, deptIds={}",
                    userId, maxScope, info.isFilter(), info.getDeptIds());
        }
        catch (Exception e) {
            log.warn("数据权限解析异常，跳过过滤: {}", e.getMessage());
            info.setFilter(false);
        }

        DataScopeContext.set(info);
    }

    @After("@annotation(cn.muziseo.common.db.annotation.DataScope)")
    public void doAfter() {
        DataScopeContext.clear();
    }

    private List<Long> parseCustomDeptIds(String json) {
        if (json == null || json.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<Long>>() {});
        }
        catch (Exception e) {
            log.warn("解析自定义部门ID失败: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}

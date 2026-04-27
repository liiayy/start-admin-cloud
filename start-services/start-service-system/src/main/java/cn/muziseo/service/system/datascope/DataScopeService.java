package cn.muziseo.service.system.datascope;

import cn.muziseo.common.core.domain.dto.DataScopeInfo;
import cn.muziseo.service.system.module.auth.manager.UserManager;
import cn.muziseo.service.system.module.auth.repository.entity.UserEntity;
import cn.muziseo.service.system.module.organization.manager.DeptManager;
import cn.muziseo.service.system.module.permission.repository.entity.RoleEntity;
import cn.muziseo.service.system.module.permission.service.RoleService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * 数据权限业务逻辑
 *
 * @author 木子软件
 */
@Service
@Slf4j
public class DataScopeService {

    @Resource
    private UserManager userManager;

    @Resource
    private RoleService roleService;

    @Resource
    private DeptManager deptManager;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 获取指定用户的数据权限范围
     */
    public DataScopeInfo getDataScopeInfo(Long userId) {
        DataScopeInfo info = new DataScopeInfo();

        try {
            // 1. 获取用户实体
            UserEntity user = userManager.getById(userId);
            if (user == null) {
                info.setFilter(false);
                return info;
            }

            // 2. 获取用户角色列表，找到范围最大的
            List<RoleEntity> roles = roleService.getRolesByUserId(userId);
            if (roles.isEmpty()) {
                info.setFilter(false);
                return info;
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

            // 3. 根据范围计算 deptIds
            switch (maxScope) {
                case 1: // 全部
                    info.setFilter(false);
                    break;
                case 2: // 自定义
                    info.setFilter(true);
                    info.setDeptIds(parseCustomDeptIds(customDeptIds));
                    break;
                case 3: // 本部门
                    info.setFilter(true);
                    info.setDeptIds(user.getDeptId() != null
                            ? List.of(user.getDeptId())
                            : Collections.emptyList());
                    break;
                case 4: // 本部门及以下
                    info.setFilter(true);
                    info.setDeptIds(deptManager.getDeptAndChildIds(user.getDeptId()));
                    break;
                case 5: // 仅本人
                    info.setFilter(true);
                    info.setUserIds(List.of(userId));
                    break;
                default:
                    info.setFilter(false);
                    break;
            }
        } catch (Exception e) {
            log.warn("分析用户数据权限失败: userId={}, err={}", userId, e.getMessage());
            info.setFilter(false);
        }
        return info;
    }

    private List<Long> parseCustomDeptIds(String json) {
        if (json == null || json.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<Long>>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}

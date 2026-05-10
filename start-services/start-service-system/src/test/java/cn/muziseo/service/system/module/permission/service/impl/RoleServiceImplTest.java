package cn.muziseo.service.system.module.permission.service.impl;

import cn.muziseo.service.system.module.auth.manager.UserRoleManager;
import cn.muziseo.service.system.module.auth.service.SaSessionRefreshService;
import cn.muziseo.service.system.module.permission.controller.request.RoleCreateRequest;
import cn.muziseo.service.system.module.permission.manager.RoleManager;
import cn.muziseo.service.system.module.permission.manager.RoleMenuManager;
import cn.muziseo.service.system.module.permission.repository.entity.RoleEntity;
import cn.muziseo.service.system.module.permission.repository.entity.RoleMenuEntity;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

/**
 * RoleServiceImpl 单元测试
 * <p>
 * 覆盖角色查询、新增、菜单分配等功能
 *
 * @author 木子软件
 */
@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {

    @Mock
    private RoleManager roleManager;

    @Mock
    private UserRoleManager userRoleManager;

    @Mock
    private RoleMenuManager roleMenuManager;

    @Mock
    private SaSessionRefreshService saSessionRefreshService;

    @InjectMocks
    private RoleServiceImpl roleService;

    // ==================== getRolesByUserId ====================

    @Nested
    @DisplayName("getRolesByUserId - 根据用户ID查询角色")
    class GetRolesByUserIdTests {

        @Test
        @DisplayName("用户无角色时返回空列表")
        void getRolesByUserId_noRoles_returnsEmptyList() {
            when(userRoleManager.getRoleIdsByUserId(1L)).thenReturn(List.of());

            List<RoleEntity> result = roleService.getRolesByUserId(1L);

            assertThat(result).isEmpty();
            verify(roleManager, never()).listByIds(anyList());
        }

        @Test
        @DisplayName("用户有角色时返回角色列表")
        void getRolesByUserId_hasRoles_returnsRoleList() {
            // Given
            List<Long> roleIds = List.of(10L, 20L);
            when(userRoleManager.getRoleIdsByUserId(1L)).thenReturn(roleIds);

            RoleEntity role1 = RoleEntity.builder().id(10L).name("管理员").code("admin").build();
            RoleEntity role2 = RoleEntity.builder().id(20L).name("操作员").code("operator").build();
            when(roleManager.listByIds(roleIds)).thenReturn(List.of(role1, role2));

            // When
            List<RoleEntity> result = roleService.getRolesByUserId(1L);

            // Then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getCode()).isEqualTo("admin");
            assertThat(result.get(1).getCode()).isEqualTo("operator");
        }
    }

    // ==================== createRole ====================

    @Nested
    @DisplayName("createRole - 新增角色")
    class AddRoleTests {

        @Test
        @DisplayName("正常新增角色")
        void createRole_validRequest_savesRole() {
            // Given
            RoleCreateRequest request = new RoleCreateRequest();
            request.setName("测试角色");
            request.setCode("test_role");
            request.setSort(1);
            request.setStatus(0);
            when(roleManager.save(any(RoleEntity.class))).thenReturn(true);

            // When
            roleService.createRole(request);

            // Then
            ArgumentCaptor<RoleEntity> captor = ArgumentCaptor.forClass(RoleEntity.class);
            verify(roleManager).save(captor.capture());

            RoleEntity saved = captor.getValue();
            assertThat(saved.getName()).isEqualTo("测试角色");
            assertThat(saved.getCode()).isEqualTo("test_role");
            assertThat(saved.getSort()).isEqualTo(1);
        }
    }

    // ==================== assignMenus ====================

    @Nested
    @DisplayName("assignMenus - 分配角色菜单")
    class AssignMenusTests {

        @Test
        @DisplayName("分配菜单时先删除旧关联再插入新关联")
        void assignMenus_withMenuIds_deletesOldAndInsertsNew() {
            // Given
            Long roleId = 1L;
            List<Long> menuIds = List.of(100L, 200L, 300L);
            when(userRoleManager.getUserIdsByRoleId(roleId)).thenReturn(List.of(10L, 20L));
            when(roleMenuManager.saveBatch(anyList())).thenReturn(true);

            // When
            roleService.assignMenus(roleId, menuIds);

            // Then
            // 1. 删除旧关联
            verify(roleMenuManager).deleteByRoleId(roleId);

            // 2. 插入新关联
            ArgumentCaptor<List<RoleMenuEntity>> captor = ArgumentCaptor.forClass(List.class);
            verify(roleMenuManager).saveBatch(captor.capture());
            List<RoleMenuEntity> saved = captor.getValue();
            assertThat(saved).hasSize(3);
            assertThat(saved.get(0).getRoleId()).isEqualTo(roleId);
            assertThat(saved.get(0).getMenuId()).isEqualTo(100L);

            // 3. 刷新 Session
            verify(saSessionRefreshService).refreshUserSessions(List.of(10L, 20L));
        }

        @Test
        @DisplayName("分配空菜单列表时只删除不插入")
        void assignMenus_emptyMenuIds_onlyDeletes() {
            when(userRoleManager.getUserIdsByRoleId(1L)).thenReturn(List.of());

            roleService.assignMenus(1L, List.of());

            verify(roleMenuManager).deleteByRoleId(1L);
            verify(roleMenuManager, never()).saveBatch(anyList());
        }

        @Test
        @DisplayName("分配 null 菜单列表时只删除不插入")
        void assignMenus_nullMenuIds_onlyDeletes() {
            when(userRoleManager.getUserIdsByRoleId(1L)).thenReturn(List.of());

            roleService.assignMenus(1L, null);

            verify(roleMenuManager).deleteByRoleId(1L);
            verify(roleMenuManager, never()).saveBatch(anyList());
        }

        @Test
        @DisplayName("分配菜单后刷新拥有该角色的用户 Session")
        void assignMenus_refreshesAffectedUserSessions() {
            // Given
            List<Long> userIds = List.of(10L, 20L, 30L);
            when(userRoleManager.getUserIdsByRoleId(1L)).thenReturn(userIds);
            when(roleMenuManager.saveBatch(anyList())).thenReturn(true);

            // When
            roleService.assignMenus(1L, List.of(100L));

            // Then
            verify(saSessionRefreshService).refreshUserSessions(userIds);
        }
    }
}

package cn.muziseo.service.system.module.permission.service.impl;

import cn.muziseo.common.core.exception.BusinessException;
import cn.muziseo.common.log.utils.DataTracerUtils;
import cn.muziseo.service.system.enums.MenuErrorCode;
import cn.muziseo.service.system.module.auth.manager.UserRoleManager;
import cn.muziseo.service.system.module.auth.service.SaSessionRefreshService;
import cn.muziseo.service.system.module.permission.controller.request.MenuCreateRequest;
import cn.muziseo.service.system.module.permission.controller.request.MenuUpdateRequest;
import cn.muziseo.service.system.module.permission.controller.vo.MenuTreeVO;
import cn.muziseo.service.system.module.permission.controller.vo.MenuVO;
import cn.muziseo.service.system.module.permission.convert.MenuConverter;
import cn.muziseo.service.system.module.permission.manager.MenuManager;
import cn.muziseo.service.system.module.permission.manager.RoleMenuManager;
import cn.muziseo.service.system.module.permission.repository.entity.MenuEntity;
import com.mybatisflex.core.query.QueryWrapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * MenuServiceImpl 单元测试
 *
 * @author 木子软件
 */
@Tag("dev")
@Tag("test")
@ExtendWith(MockitoExtension.class)
class MenuServiceImplTest {

    @Mock
    private MenuManager menuManager;

    @Mock
    private RoleMenuManager roleMenuManager;

    @Mock
    private UserRoleManager userRoleManager;

    @Mock
    private SaSessionRefreshService saSessionRefreshService;

    @Mock
    private MenuConverter menuConverter;

    @InjectMocks
    private MenuServiceImpl menuService;

    @Nested
    @DisplayName("getMenusByRoleIds - 根据角色获取菜单")
    class GetMenusByRoleIdsTests {
        @Test
        @DisplayName("角色ID列表为空时返回空列表")
        void getMenusByRoleIds_empty_returnsEmptyList() {
            List<MenuVO> result = menuService.getMenusByRoleIds(Collections.emptyList());
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("角色关联菜单为空时返回空列表")
        void getMenusByRoleIds_noMenus_returnsEmptyList() {
            when(roleMenuManager.getMenuIdsByRoleIds(List.of(1L))).thenReturn(Collections.emptyList());
            List<MenuVO> result = menuService.getMenusByRoleIds(List.of(1L));
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("正常获取菜单列表")
        void getMenusByRoleIds_normal_returnsList() {
            when(roleMenuManager.getMenuIdsByRoleIds(List.of(1L))).thenReturn(List.of(100L));
            MenuEntity menu = new MenuEntity();
            menu.setId(100L);
            when(menuManager.listByIds(List.of(100L))).thenReturn(List.of(menu));

            MenuVO vo = MenuVO.builder().id(100L).build();
            when(menuConverter.toVO(menu)).thenReturn(vo);

            List<MenuVO> result = menuService.getMenusByRoleIds(List.of(1L));
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo(100L);
        }
    }

    @Nested
    @DisplayName("getUserMenuTree - 获取用户菜单树")
    class GetUserMenuTreeTests {
        @Test
        @DisplayName("超级管理员获取全量菜单树")
        void getUserMenuTree_superAdmin_returnsTree() {
            // Super Admin ID is 1L
            MenuEntity m1 = new MenuEntity();
            m1.setId(10L);
            m1.setParentId(0L);
            m1.setSort(1);

            when(menuManager.list(any(QueryWrapper.class))).thenReturn(List.of(m1));

            MenuTreeVO treeVo = new MenuTreeVO();
            treeVo.setId(10L);
            treeVo.setParentId(0L);
            when(menuConverter.toTreeVO(m1)).thenReturn(treeVo);

            List<MenuTreeVO> result = menuService.getUserMenuTree(1L);
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo(10L);
        }

        @Test
        @DisplayName("普通用户角色列表为空时返回空")
        void getUserMenuTree_normalUserNoRoles_returnsEmpty() {
            when(userRoleManager.getRoleIdsByUserId(2L)).thenReturn(Collections.emptyList());
            List<MenuTreeVO> result = menuService.getUserMenuTree(2L);
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("普通用户菜单列表为空时返回空")
        void getUserMenuTree_normalUserNoMenus_returnsEmpty() {
            when(userRoleManager.getRoleIdsByUserId(2L)).thenReturn(List.of(5L));
            when(roleMenuManager.getMenuIdsByRoleIds(List.of(5L))).thenReturn(Collections.emptyList());
            List<MenuTreeVO> result = menuService.getUserMenuTree(2L);
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("getMenu - 获取菜单详情")
    class GetMenuTests {
        @Test
        @DisplayName("菜单存在时返回详情")
        void getMenu_existing_returnsVO() {
            MenuEntity menu = new MenuEntity();
            menu.setId(1L);
            when(menuManager.getById(1L)).thenReturn(menu);

            MenuVO vo = MenuVO.builder().id(1L).build();
            when(menuConverter.toVO(menu)).thenReturn(vo);

            MenuVO result = menuService.getMenu(1L);
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("菜单不存在时抛出 BusinessException")
        void getMenu_nonexistent_throwsException() {
            when(menuManager.getById(99L)).thenReturn(null);

            assertThatThrownBy(() -> menuService.getMenu(99L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", MenuErrorCode.MENU_NOT_EXISTS);
        }
    }

    @Nested
    @DisplayName("createMenu - 创建菜单")
    class CreateMenuTests {
        @Test
        @DisplayName("权限标识已存在时抛出异常")
        void createMenu_permissionExists_throwsException() {
            MenuCreateRequest request = new MenuCreateRequest();
            request.setPermission("system:user:list");
            when(menuManager.existsByPermission("system:user:list", null)).thenReturn(true);

            assertThatThrownBy(() -> menuService.createMenu(request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", MenuErrorCode.MENU_PERMISSION_EXISTS);
        }

        @Test
        @DisplayName("正常创建菜单")
        void createMenu_normal_success() {
            try (MockedStatic<DataTracerUtils> dataTracer = mockStatic(DataTracerUtils.class)) {
                MenuCreateRequest request = new MenuCreateRequest();
                request.setPermission("system:user:list");
                when(menuManager.existsByPermission("system:user:list", null)).thenReturn(false);

                MenuEntity entity = new MenuEntity();
                entity.setId(10L);
                when(menuConverter.toEntity(request)).thenReturn(entity);

                menuService.createMenu(request);

                verify(menuManager, times(1)).save(entity);
                dataTracer.verify(() -> DataTracerUtils.insert(10L, cn.muziseo.common.core.datatracer.DataTracerTypeEnum.MENU), times(1));
            }
        }
    }

    @Nested
    @DisplayName("deleteMenu - 删除菜单")
    class DeleteMenuTests {
        @Test
        @DisplayName("菜单不存在时抛出异常")
        void deleteMenu_nonexistent_throwsException() {
            when(menuManager.getById(99L)).thenReturn(null);

            assertThatThrownBy(() -> menuService.deleteMenu(99L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", MenuErrorCode.MENU_NOT_EXISTS);
        }

        @Test
        @DisplayName("存在子菜单时抛出异常")
        void deleteMenu_hasChildren_throwsException() {
            MenuEntity menu = new MenuEntity();
            menu.setId(10L);
            when(menuManager.getById(10L)).thenReturn(menu);
            when(menuManager.listByParentId(10L)).thenReturn(List.of(new MenuEntity()));

            assertThatThrownBy(() -> menuService.deleteMenu(10L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", MenuErrorCode.MENU_HAS_CHILDREN);
        }

        @Test
        @DisplayName("正常删除菜单并刷新受影响的用户 Session")
        void deleteMenu_normal_success() {
            try (MockedStatic<DataTracerUtils> dataTracer = mockStatic(DataTracerUtils.class)) {
                MenuEntity menu = new MenuEntity();
                menu.setId(10L);
                when(menuManager.getById(10L)).thenReturn(menu);
                when(menuManager.listByParentId(10L)).thenReturn(Collections.emptyList());

                when(roleMenuManager.getRoleIdsByMenuId(10L)).thenReturn(List.of(1L));
                when(userRoleManager.getUserIdsByRoleIds(List.of(1L))).thenReturn(List.of(100L));

                menuService.deleteMenu(10L);

                verify(roleMenuManager, times(1)).deleteByMenuId(10L);
                verify(menuManager, times(1)).removeById(10L);
                verify(saSessionRefreshService, times(1)).refreshUserSessions(List.of(100L));
                dataTracer.verify(() -> DataTracerUtils.delete(10L, cn.muziseo.common.core.datatracer.DataTracerTypeEnum.MENU), times(1));
            }
        }
    }
}

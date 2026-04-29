package cn.muziseo.service.system.module.auth.service.impl;

import cn.muziseo.common.core.utils.string.StringUtils;
import cn.muziseo.common.cache.config.ConfigUtils;

import cn.hutool.core.bean.BeanUtil;
import cn.muziseo.common.core.exception.BusinessException;
import cn.muziseo.common.core.util.UserSecurityUtils;
import cn.muziseo.common.db.annotation.DataScope;
import cn.muziseo.common.db.page.PageResponse;
import cn.muziseo.common.satoken.core.util.PasswordUtils;
import cn.muziseo.service.system.enums.UserErrorCode;
import cn.muziseo.service.system.module.auth.controller.request.*;
import cn.muziseo.service.system.module.auth.controller.vo.UserVO;
import cn.muziseo.service.system.module.auth.manager.UserManager;
import cn.muziseo.service.system.module.auth.manager.UserRoleManager;
import cn.muziseo.service.system.module.auth.controller.vo.UserImportVO;
import cn.muziseo.service.system.module.auth.repository.entity.UserEntity;
import cn.muziseo.service.system.module.auth.repository.entity.UserRoleEntity;
import cn.muziseo.common.cache.datascope.DataScopeCacheManager;
import cn.muziseo.service.system.module.auth.service.SaSessionRefreshService;
import cn.muziseo.service.system.module.auth.service.UserService;
import cn.muziseo.service.system.module.organization.manager.DeptManager;
import cn.muziseo.service.system.module.organization.repository.entity.DeptEntity;
import cn.muziseo.service.system.module.permission.service.MenuService;
import cn.muziseo.service.system.module.permission.service.RoleService;
import cn.muziseo.service.system.module.auth.convert.UserConverter;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 用户业务实现
 *
 * @author 木子软件
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Resource
    private UserManager userManager;

    @Resource
    private DeptManager deptManager;

    @Resource
    private UserRoleManager userRoleManager;

    @Resource
    private RoleService roleService;

    @Resource
    private MenuService menuService;

    @Resource
    private SaSessionRefreshService saSessionRefreshService;

    @Resource
    private UserConverter userConverter;

    /**
     * 根据用户名获取用户实体
     *
     * @param username 用户名
     * @return 用户实体
     */
    @Override
    public UserEntity getByUsername(String username) {
        return userManager.getByUsername(username);
    }

    /**
     * 根据用户 ID 获取用户实体
     *
     * @param id 用户 ID
     * @return 用户实体
     */
    @Override
    public UserEntity getUserById(Long id) {
        return userManager.getById(id);
    }

    /**
     * 分页查询用户列表
     *
     * @param request 用户分页查询请求
     * @return 用户分页结果
     */
    @Override
    @DataScope
    public PageResponse<UserVO> pageUser(UserPageRequest request) {
        var page = userManager.pageUser(request);
        List<UserEntity> records = page.getRecords();
        if (records.isEmpty()) {
            return new PageResponse<>(List.of(), 0);
        }

        List<UserVO> voList = buildUserVOList(records);

        PageResponse<UserVO> response = new PageResponse<>();
        response.setList(voList);
        response.setTotal(page.getTotalRow());
        return response;
    }

    /**
     * 获取用户列表（不分页）
     *
     * @param request 用户查询请求
     * @return 用户视图对象列表
     */
    @Override
    @DataScope
    public List<UserVO> listUser(UserPageRequest request) {
        List<UserEntity> records = userManager.listUser(request);
        if (records.isEmpty()) {
            return List.of();
        }
        return buildUserVOList(records);
    }

    /**
     * 获取用户详情
     *
     * @param id 用户 ID
     * @return 用户视图对象
     */
    @Override
    public UserVO getUser(Long id) {
        UserEntity entity = userManager.getById(id);
        if (entity == null) {
            throw new BusinessException(UserErrorCode.USER_NOT_EXISTS);
        }
        return toUserVO(entity);
    }

    /**
     * 创建用户
     * <p>
     * 1. 校验用户名、手机号、邮箱唯一性
     * 2. 加密密码并保存
     *
     * @param request 创建用户请求
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createUser(UserAddRequest request) {
        // 1. 校验唯一性
        if (userManager.existsByUsername(request.getUsername(), null)) {
            throw new BusinessException(UserErrorCode.USERNAME_EXISTS);
        }
        if (userManager.existsByMobile(request.getMobile(), null)) {
            throw new BusinessException(UserErrorCode.PHONE_EXISTS);
        }
        if (userManager.existsByEmail(request.getEmail(), null)) {
            throw new BusinessException(UserErrorCode.EMAIL_EXISTS);
        }

        UserEntity entity = userConverter.toEntity(request);
        entity.setPassword(PasswordUtils.encode(request.getPassword()));
        if (entity.getStatus() == null) {
            entity.setStatus(0);
        }
        userManager.save(entity);
        log.info("创建用户成功: id={}, username={}", entity.getId(), entity.getUsername());
    }

    /**
     * 更新用户基本信息
     * <p>
     * 1. 校验手机号、邮箱唯一性
     * 2. 更新用户信息并根据需要刷新数据权限缓存
     *
     * @param request 更新用户请求
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(UserUpdateRequest request) {
        UserEntity existing = userManager.getById(request.getId());
        if (existing == null) {
            throw new BusinessException(UserErrorCode.USER_NOT_EXISTS);
        }

        // 1. 校验唯一性
        if (userManager.existsByMobile(request.getMobile(), request.getId())) {
            throw new BusinessException(UserErrorCode.PHONE_EXISTS);
        }
        if (userManager.existsByEmail(request.getEmail(), request.getId())) {
            throw new BusinessException(UserErrorCode.EMAIL_EXISTS);
        }

        UserEntity entity = userConverter.toEntity(request);
        userManager.updateById(entity);
        
        // 如果修改了部门，需要刷新数据权限缓存
        if (request.getDeptId() != null && !Objects.equals(request.getDeptId(), existing.getDeptId())) {
            DataScopeCacheManager.evictCache(request.getId());
        }
        log.info("更新用户成功: id={}", request.getId());
    }

    /**
     * 删除用户
     * <p>
     * 1. 校验是否为超级管理员（受保护）
     * 2. 删除关联的角色关系
     * 3. 删除用户记录
     *
     * @param id 用户 ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long id) {
        if (UserSecurityUtils.isSuperAdmin(id)) {
            throw new BusinessException(UserErrorCode.SUPER_ADMIN_PROTECTED);
        }
        userRoleManager.deleteByUserId(id);
        userManager.removeById(id);
        log.info("删除用户成功: id={}", id);
    }

    /**
     * 更新用户状态
     *
     * @param request 更新状态请求
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(UserUpdateStatusRequest request) {
        if (UserSecurityUtils.isSuperAdmin(request.getId())) {
            throw new BusinessException(UserErrorCode.SUPER_ADMIN_PROTECTED);
        }
        UserEntity existing = userManager.getById(request.getId());
        if (existing == null) {
            throw new BusinessException(UserErrorCode.USER_NOT_EXISTS);
        }
        UserEntity entity = new UserEntity();
        entity.setId(request.getId());
        entity.setStatus(request.getStatus());
        userManager.updateById(entity);
        log.info("更新用户状态: id={}, status={}", request.getId(), request.getStatus());
    }

    /**
     * 重置用户密码
     *
     * @param request 重置密码请求
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(UserResetPasswordRequest request) {
        UserEntity existing = userManager.getById(request.getId());
        if (existing == null) {
            throw new BusinessException(UserErrorCode.USER_NOT_EXISTS);
        }
        UserEntity entity = new UserEntity();
        entity.setId(request.getId());
        entity.setPassword(PasswordUtils.encode(request.getNewPassword()));
        userManager.updateById(entity);
        log.info("重置用户密码: id={}", request.getId());
    }

    /**
     * 用户修改自身密码
     *
     * @param request 修改密码请求
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePassword(UserUpdatePasswordRequest request) {
        Long userId = cn.dev33.satoken.stp.StpUtil.getLoginIdAsLong();
        UserEntity existing = userManager.getById(userId);
        if (existing == null) {
            throw new BusinessException(UserErrorCode.USER_NOT_EXISTS);
        }
        if (!PasswordUtils.matches(request.getOldPassword(), existing.getPassword())) {
            throw new BusinessException(UserErrorCode.OLD_PASSWORD_ERROR);
        }
        UserEntity entity = new UserEntity();
        entity.setId(userId);
        entity.setPassword(PasswordUtils.encode(request.getNewPassword()));
        userManager.updateById(entity);
        log.info("用户修改密码成功: userId={}", userId);
    }

    /**
     * 分配用户角色
     *
     * @param request 分配角色请求
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRole(UserRoleAssignRequest request) {
        userRoleManager.deleteByUserId(request.getUserId());
        userRoleManager.batchInsert(request.getUserId(), request.getRoleIds());
        
        // 刷新权限相关的缓存
        saSessionRefreshService.refreshUserSession(request.getUserId());
        DataScopeCacheManager.evictCache(request.getUserId());
        
        log.info("分配用户角色: userId={}, roleIds={}", request.getUserId(), request.getRoleIds());
    }

    /**
     * 批量导入用户
     *
     * @param list          导入用户数据列表
     * @param updateSupport 是否支持更新已存在的数据
     * @return 导入结果描述
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String importUsers(List<UserImportVO> list, boolean updateSupport) {
        if (list == null || list.isEmpty()) {
            throw new BusinessException("导入用户数据不能为空！");
        }

        List<String> errorList = new ArrayList<>();

        // 预取部门数据
        Map<String, Long> deptMap = deptManager.listAll().stream()
                .collect(Collectors.toMap(
                    d -> d.getName().trim().toLowerCase(),
                    DeptEntity::getId,
                    (v1, v2) -> v1
                ));

        String initPassword = ConfigUtils.getString("sys.user.initPassword", "123456");
        int successCount = 0;

        for (int i = 0; i < list.size(); i++) {
            UserImportVO user = list.get(i);
            try {
                UserEntity existing = userManager.getByUsername(user.getUsername());
                if (existing == null) {
                    UserEntity entity = userConverter.toEntity(user);
                    entity.setPassword(PasswordUtils.encode(initPassword));
                    resolveDept(entity, user.getDeptName(), deptMap);
                    userManager.save(entity);
                    successCount++;
                } else if (updateSupport) {
                    userConverter.copyToEntity(user, existing);
                    resolveDept(existing, user.getDeptName(), deptMap);
                    userManager.updateById(existing);
                    successCount++;
                } else {
                    errorList.add("第 " + (i + 2) + " 行：账号 " + user.getUsername() + " 已存在");
                }
            } catch (Exception e) {
                errorList.add("第 " + (i + 2) + " 行：账号 " + user.getUsername() + " 导入异常：" + e.getMessage());
            }
        }

        if (!errorList.isEmpty()) {
            StringBuilder sb = new StringBuilder("导入失败！检测到 " + errorList.size() + " 处错误，已全部回滚：");
            for (String error : errorList) {
                sb.append(error);
            }
            throw new BusinessException(sb.toString());
        }

        return "导入成功，共导入 " + successCount + " 条数据";
    }

    private void resolveDept(UserEntity entity, String deptName, Map<String, Long> deptMap) {
        if (StringUtils.isNotBlank(deptName)) {
            Long deptId = deptMap.get(deptName.trim().toLowerCase());
            if (deptId != null) {
                entity.setDeptId(deptId);
            }
        }
    }

    private UserVO toUserVO(UserEntity entity) {
        return toUserVO(entity, null, null);
    }

    private List<UserVO> buildUserVOList(List<UserEntity> records) {
        // 1. 批量获取部门名称
        List<Long> deptIds = records.stream()
                .map(UserEntity::getDeptId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, String> deptMap = deptManager.listByIds(deptIds).stream()
                .collect(Collectors.toMap(DeptEntity::getId, DeptEntity::getName));

        // 2. 批量获取用户角色
        List<Long> userIds = records.stream()
                .map(UserEntity::getId)
                .collect(Collectors.toList());
        Map<Long, List<Long>> userRoleMap = userRoleManager.listByUserIds(userIds).stream()
                .collect(Collectors.groupingBy(
                        UserRoleEntity::getUserId,
                        Collectors.mapping(UserRoleEntity::getRoleId, Collectors.toList())
                ));

        return records.stream()
                .map(entity -> toUserVO(entity, deptMap, userRoleMap))
                .collect(Collectors.toList());
    }

    /**
     * 将 Entity 转换为 VO，支持传入预取的缓存数据以优化性能
     */
    private UserVO toUserVO(UserEntity entity, Map<Long, String> deptMap, Map<Long, List<Long>> userRoleMap) {
        // 解析部门名称
        String deptName = null;
        if (deptMap != null) {
            deptName = deptMap.get(entity.getDeptId());
        } else if (entity.getDeptId() != null) {
            DeptEntity dept = deptManager.getById(entity.getDeptId());
            if (dept != null) {
                deptName = dept.getName();
            }
        }

        // 获取用户角色ID列表
        List<Long> roleIds;
        if (userRoleMap != null) {
            roleIds = userRoleMap.getOrDefault(entity.getId(), List.of());
        } else {
            roleIds = userRoleManager.getRoleIdsByUserId(entity.getId());
        }

        return UserVO.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .nickname(entity.getNickname())
                .deptId(entity.getDeptId())
                .deptName(deptName)
                .postIds(entity.getPostIds())
                .roleIds(roleIds)
                .mobile(entity.getMobile())
                .email(entity.getEmail())
                .sex(entity.getSex())
                .avatar(entity.getAvatar())
                .status(entity.getStatus())
                .loginIp(entity.getLoginIp())
                .loginDate(entity.getLoginDate())
                .createTime(entity.getCreateTime())
                .build();
    }
}

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
import cn.muziseo.common.excel.core.ExcelResult;
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
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    public UserEntity getByUsername(String username) {
        return userManager.getByUsername(username);
    }

    @Override
    public UserEntity getUserById(Long id) {
        return userManager.getById(id);
    }

    @Override
    @DataScope
    public PageResponse<UserVO> pageUser(UserPageRequest request) {
        var page = userManager.pageUser(request);
        List<UserEntity> records = page.getRecords();
        if (records.isEmpty()) {
            return new PageResponse<>(List.of(), 0);
        }

        // 1. 批量获取部门名称 (O(1) 替代 O(N))
        List<Long> deptIds = records.stream()
                .map(UserEntity::getDeptId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, String> deptMap = deptManager.listByIds(deptIds).stream()
                .collect(Collectors.toMap(DeptEntity::getId, DeptEntity::getName));

        // 2. 批量获取用户角色 (O(1) 替代 O(N))
        List<Long> userIds = records.stream()
                .map(UserEntity::getId)
                .collect(Collectors.toList());
        Map<Long, List<Long>> userRoleMap = userRoleManager.listByUserIds(userIds).stream()
                .collect(Collectors.groupingBy(
                        UserRoleEntity::getUserId,
                        Collectors.mapping(UserRoleEntity::getRoleId, Collectors.toList())
                ));

        List<UserVO> voList = records.stream()
                .map(entity -> toUserVO(entity, deptMap, userRoleMap))
                .collect(Collectors.toList());

        PageResponse<UserVO> response = new PageResponse<>();
        response.setList(voList);
        response.setTotal(page.getTotalRow());
        return response;
    }

    @Override
    public UserVO getUser(Long id) {
        UserEntity entity = userManager.getById(id);
        if (entity == null) {
            throw new BusinessException(UserErrorCode.USER_NOT_EXISTS);
        }
        return toUserVO(entity);
    }

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

        UserEntity entity = BeanUtil.copyProperties(request, UserEntity.class);
        entity.setPassword(PasswordUtils.encode(request.getPassword()));
        if (entity.getStatus() == null) {
            entity.setStatus(0);
        }
        userManager.save(entity);
        log.info("创建用户成功: id={}, username={}", entity.getId(), entity.getUsername());
    }

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

        UserEntity entity = new UserEntity();
        entity.setId(request.getId());
        entity.setNickname(request.getNickname());
        entity.setDeptId(request.getDeptId());
        entity.setPostIds(request.getPostIds());
        entity.setMobile(request.getMobile());
        entity.setEmail(request.getEmail());
        entity.setSex(request.getSex());
        entity.setAvatar(request.getAvatar());
        entity.setRemark(request.getRemark());
        userManager.updateById(entity);
        
        // 如果修改了部门，需要刷新数据权限缓存
        if (request.getDeptId() != null && !Objects.equals(request.getDeptId(), existing.getDeptId())) {
            DataScopeCacheManager.evictCache(request.getId());
        }
        log.info("更新用户成功: id={}", request.getId());
    }

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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExcelResult<Void> importUsers(List<UserImportVO> list, boolean updateSupport) {
        if (list == null || list.isEmpty()) {
            throw new BusinessException("导入用户数据不能为空！");
        }

        ExcelResult<Void> result = new ExcelResult<>();
        
        // 1. 预取部门数据 (优化1: 批量查询替代循环内单条查询; 优化2: 忽略大小写和前后空格)
        Map<String, Long> deptMap = deptManager.listAll().stream()
                .collect(Collectors.toMap(
                    d -> d.getName().trim().toLowerCase(), 
                    DeptEntity::getId, 
                    (v1, v2) -> v1
                ));

        String initPassword = ConfigUtils.getString("sys.user.initPassword", "123456");

        for (int i = 0; i < list.size(); i++) {
            UserImportVO user = list.get(i);
            try {
                // 验证是否存在用户
                UserEntity u = userManager.getByUsername(user.getUsername());
                if (u == null) {
                    u = BeanUtil.copyProperties(user, UserEntity.class);
                    u.setPassword(PasswordUtils.encode(initPassword));
                    // 解析部门
                    if (StringUtils.isNotBlank(user.getDeptName())) {
                        Long deptId = deptMap.get(user.getDeptName().trim().toLowerCase());
                        if (deptId != null) {
                            u.setDeptId(deptId);
                        }
                    }
                    userManager.save(u);
                } else if (updateSupport) {
                    Long userId = u.getId();
                    u = BeanUtil.copyProperties(user, UserEntity.class);
                    u.setId(userId);
                    // 解析部门
                    if (StringUtils.isNotBlank(user.getDeptName())) {
                        Long deptId = deptMap.get(user.getDeptName().trim().toLowerCase());
                        if (deptId != null) {
                            u.setDeptId(deptId);
                        }
                    }
                    userManager.updateById(u);
                } else {
                    result.getErrorList().add("第 " + (i + 2) + " 行：账号 " + user.getUsername() + " 已存在");
                }
            } catch (Exception e) {
                result.getErrorList().add("第 " + (i + 2) + " 行：账号 " + user.getUsername() + " 导入异常：" + e.getMessage());
            }
        }

        if (!result.getErrorList().isEmpty()) {
            // 优化5: 结构化错误反馈。抛出异常以回滚事务
            StringBuilder sb = new StringBuilder("导入失败！检测到 " + result.getErrorList().size() + " 处错误，已全部回滚：");
            for (String error : result.getErrorList()) {
                sb.append("<br/>").append(error);
            }
            throw new BusinessException(sb.toString());
        }

        return result;
    }

    private UserVO toUserVO(UserEntity entity) {
        return toUserVO(entity, null, null);
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

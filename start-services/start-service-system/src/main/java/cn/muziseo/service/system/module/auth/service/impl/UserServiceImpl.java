package cn.muziseo.service.system.module.auth.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.muziseo.common.core.exception.BusinessException;
import cn.muziseo.common.db.annotation.DataScope;
import cn.muziseo.common.db.page.PageResponse;
import cn.muziseo.common.satoken.core.util.PasswordUtils;
import cn.muziseo.service.system.enums.UserErrorCode;
import cn.muziseo.service.system.module.auth.controller.request.*;
import cn.muziseo.service.system.module.auth.controller.vo.UserVO;
import cn.muziseo.service.system.module.auth.manager.UserManager;
import cn.muziseo.service.system.module.auth.manager.UserRoleManager;
import cn.muziseo.service.system.module.auth.repository.entity.UserEntity;
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
        List<UserVO> voList = page.getRecords().stream()
                .map(this::toUserVO)
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
        if (userManager.existsByUsername(request.getUsername())) {
            throw new BusinessException(UserErrorCode.USERNAME_EXISTS);
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
        log.info("更新用户成功: id={}", request.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long id) {
        UserEntity existing = userManager.getById(id);
        if (existing == null) {
            throw new BusinessException(UserErrorCode.USER_NOT_EXISTS);
        }
        userRoleManager.deleteByUserId(id);
        userManager.removeById(id);
        log.info("删除用户成功: id={}", id);
    }

    @Override
    public void updateStatus(UserUpdateStatusRequest request) {
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
        saSessionRefreshService.refreshUserSession(request.getUserId());
        log.info("分配用户角色: userId={}, roleIds={}", request.getUserId(), request.getRoleIds());
    }

    private UserVO toUserVO(UserEntity entity) {
        // 解析部门名称
        String deptName = null;
        if (entity.getDeptId() != null) {
            DeptEntity dept = deptManager.getById(entity.getDeptId());
            if (dept != null) {
                deptName = dept.getName();
            }
        }

        // 获取用户角色ID列表
        List<Long> roleIds = userRoleManager.getRoleIdsByUserId(entity.getId());

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

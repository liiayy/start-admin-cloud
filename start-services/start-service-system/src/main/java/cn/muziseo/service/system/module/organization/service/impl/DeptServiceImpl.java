package cn.muziseo.service.system.module.organization.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.muziseo.common.core.datatracer.DataTracerTypeEnum;
import cn.muziseo.common.log.utils.DataTracerUtils;
import cn.muziseo.common.core.exception.BusinessException;
import cn.muziseo.service.system.enums.DeptErrorCode;
import cn.muziseo.service.system.module.organization.controller.request.DeptCreateRequest;
import cn.muziseo.service.system.module.organization.controller.vo.DeptTreeVO;
import cn.muziseo.service.system.module.organization.controller.vo.DeptVO;
import cn.muziseo.service.system.module.auth.manager.UserManager;
import cn.muziseo.service.system.module.organization.manager.DeptManager;
import cn.muziseo.service.system.module.organization.manager.PostManager;
import cn.muziseo.service.system.module.organization.repository.entity.DeptEntity;
import cn.muziseo.service.system.module.organization.service.DeptService;
import cn.muziseo.service.system.module.organization.convert.DeptConverter;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 部门业务实现
 *
 * @author 木子软件
 */
@Service
@Slf4j
public class DeptServiceImpl implements DeptService {

    @Resource
    private DeptManager deptManager;

    @Resource
    private UserManager userManager;

    @Resource
    private PostManager postManager;

    @Resource
    private DeptConverter deptConverter;

    /**
     * 获取所有部门列表
     *
     * @return 部门视图对象列表
     */
    @Override
    public List<DeptVO> list() {
        return deptManager.listAll().stream()
                .map(deptConverter::toVO)
                .collect(Collectors.toList());
    }

    /**
     * 获取部门树结构
     *
     * @return 部门树视图对象列表
     */
    @Override
    public List<DeptTreeVO> tree() {
        List<DeptEntity> allDepts = deptManager.listAll();
        return buildTree(allDepts, 0L);
    }

    /**
     * 根据 ID 获取部门详情
     *
     * @param id 部门 ID
     * @return 部门视图对象
     */
    @Override
    public DeptVO getById(Long id) {
        DeptEntity dept = deptManager.getById(id);
        if (dept == null) {
            throw new BusinessException(DeptErrorCode.DEPT_NOT_EXISTS);
        }
        return deptConverter.toVO(dept);
    }

    /**
     * 新增部门
     * <p>
     * 1. 校验部门名称是否唯一
     * 2. 校验父部门是否存在
     *
     * @param request 新增部门请求参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createDept(DeptCreateRequest request) {
        // 校验名称唯一
        if (deptManager.existsByName(request.getName(), null)) {
            throw new BusinessException(DeptErrorCode.DEPT_NAME_EXISTS);
        }

        // 校验父部门存在
        Long parentId = request.getParentId() != null ? request.getParentId() : 0L;
        if (!parentId.equals(0L) && deptManager.getById(parentId) == null) {
            throw new BusinessException(DeptErrorCode.DEPT_NOT_EXISTS, "父部门不存在");
        }

        DeptEntity entity = deptConverter.toEntity(request);
        if (entity.getParentId() == null) {
            entity.setParentId(0L);
        }
        if (entity.getStatus() == null) {
            entity.setStatus(0);
        }
        deptManager.save(entity);
        DataTracerUtils.insert(entity.getId(), DataTracerTypeEnum.DEPT);
        log.info("新增部门成功: id={}, name={}", entity.getId(), entity.getName());
    }

    /**
     * 修改部门
     * <p>
     * 1. 校验部门是否存在
     * 2. 校验名称是否唯一（排除自身）
     * 3. 校验父部门有效性（不能把自己设为父部门）
     *
     * @param id      部门 ID
     * @param request 修改部门请求参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDept(Long id, DeptCreateRequest request) {
        DeptEntity dept = deptManager.getById(id);
        if (dept == null) {
            throw new BusinessException(DeptErrorCode.DEPT_NOT_EXISTS);
        }

        // 校验名称唯一（排除自身）
        if (deptManager.existsByName(request.getName(), id)) {
            throw new BusinessException(DeptErrorCode.DEPT_NAME_EXISTS);
        }

        // 校验父部门存在且不能把自己设为父
        Long parentId = request.getParentId() != null ? request.getParentId() : 0L;
        if (parentId.equals(id)) {
            throw new BusinessException(DeptErrorCode.DEPT_NOT_EXISTS, "不能将自己设为父部门");
        }
        if (!parentId.equals(0L) && deptManager.getById(parentId) == null) {
            throw new BusinessException(DeptErrorCode.DEPT_NOT_EXISTS, "父部门不存在");
        }

        DeptEntity entity = deptConverter.toEntity(request);
        entity.setId(id);
        deptManager.updateById(entity);
        DeptEntity updated = deptManager.getById(id);
        DataTracerUtils.update(id, DataTracerTypeEnum.DEPT, dept, updated);
        log.info("更新部门成功: id={}", id);
    }

    /**
     * 删除部门
     * <p>
     * 1. 检查是否存在子部门
     * 2. 检查是否有关联用户
     * 3. 检查是否有关联岗位
     *
     * @param id 部门 ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDept(Long id) {
        DeptEntity dept = deptManager.getById(id);
        if (dept == null) {
            throw new BusinessException(DeptErrorCode.DEPT_NOT_EXISTS);
        }

        // 检查是否有子部门
        if (deptManager.countByParentId(id) > 0) {
            throw new BusinessException(DeptErrorCode.DEPT_HAS_CHILDREN);
        }

        // 检查是否有用户关联
        if (userManager.countByDeptId(id) > 0) {
            throw new BusinessException(DeptErrorCode.DEPT_HAS_USERS);
        }

        // 检查是否有岗位关联
        if (postManager.countByDeptId(id) > 0) {
            throw new BusinessException(DeptErrorCode.DEPT_HAS_POSTS);
        }

        deptManager.removeById(id);
        DataTracerUtils.delete(id, DataTracerTypeEnum.DEPT);
        log.info("删除部门成功: id={}", id);
    }

    /**
     * 更新部门状态
     *
     * @param id     部门 ID
     * @param status 状态值
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        DeptEntity dept = deptManager.getById(id);
        if (dept == null) {
            throw new BusinessException(DeptErrorCode.DEPT_NOT_EXISTS);
        }
        DeptEntity entity = new DeptEntity();
        entity.setId(id);
        entity.setStatus(status);
        deptManager.updateById(entity);
        DeptEntity updated = deptManager.getById(id);
        DataTracerUtils.update(id, DataTracerTypeEnum.DEPT, dept, updated);
        log.info("更新部门状态: id={}, status={}", id, status);
    }

    /**
     * 构建部门树 (O(N) 优化版)
     */
    private List<DeptTreeVO> buildTree(List<DeptEntity> allDepts, Long rootId) {
        if (allDepts == null || allDepts.isEmpty()) {
            return List.of();
        }

        // 按父 ID 分组转换为 VO
        Map<Long, List<DeptTreeVO>> parentMap = allDepts.stream()
                .map(deptConverter::toTreeVO)
                .collect(Collectors.groupingBy(DeptTreeVO::getParentId));

        // 构建嵌套结构
        List<DeptTreeVO> allVos = parentMap.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());

        allVos.forEach(vo -> {
            List<DeptTreeVO> children = parentMap.get(vo.getId());
            if (children != null) {
                vo.setChildren(children);
            }
        });

        return parentMap.getOrDefault(rootId, List.of());
    }
}

package cn.muziseo.service.system.module.organization.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.muziseo.common.core.exception.BusinessException;
import cn.muziseo.service.system.enums.DeptErrorCode;
import cn.muziseo.service.system.module.organization.controller.request.DeptAddRequest;
import cn.muziseo.service.system.module.organization.controller.vo.DeptTreeVO;
import cn.muziseo.service.system.module.organization.controller.vo.DeptVO;
import cn.muziseo.service.system.module.organization.manager.DeptManager;
import cn.muziseo.service.system.module.organization.repository.entity.DeptEntity;
import cn.muziseo.service.system.module.organization.service.DeptService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
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

    @Override
    public List<DeptVO> list() {
        return deptManager.listAll().stream()
                .map(this::toDeptVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DeptTreeVO> tree() {
        List<DeptEntity> allDepts = deptManager.listAll();
        return buildTree(allDepts, 0L);
    }

    @Override
    public DeptVO getById(Long id) {
        DeptEntity dept = deptManager.getById(id);
        if (dept == null) {
            throw new BusinessException(DeptErrorCode.DEPT_NOT_EXISTS);
        }
        return toDeptVO(dept);
    }

    @Override
    public void addDept(DeptAddRequest request) {
        // 校验名称唯一
        if (deptManager.existsByName(request.getName(), null)) {
            throw new BusinessException(DeptErrorCode.DEPT_NAME_EXISTS);
        }

        // 校验父部门存在
        Long parentId = request.getParentId() != null ? request.getParentId() : 0L;
        if (!parentId.equals(0L) && deptManager.getById(parentId) == null) {
            throw new BusinessException(DeptErrorCode.DEPT_NOT_EXISTS, "父部门不存在");
        }

        DeptEntity entity = BeanUtil.copyProperties(request, DeptEntity.class);
        if (entity.getParentId() == null) {
            entity.setParentId(0L);
        }
        if (entity.getStatus() == null) {
            entity.setStatus(0);
        }
        deptManager.save(entity);
        log.info("新增部门成功: id={}, name={}", entity.getId(), entity.getName());
    }

    @Override
    public void updateDept(Long id, DeptAddRequest request) {
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

        DeptEntity entity = BeanUtil.copyProperties(request, DeptEntity.class);
        entity.setId(id);
        deptManager.updateById(entity);
        log.info("更新部门成功: id={}", id);
    }

    @Override
    public void deleteDept(Long id) {
        DeptEntity dept = deptManager.getById(id);
        if (dept == null) {
            throw new BusinessException(DeptErrorCode.DEPT_NOT_EXISTS);
        }

        // 检查是否有子部门
        if (deptManager.countByParentId(id) > 0) {
            throw new BusinessException(DeptErrorCode.DEPT_HAS_CHILDREN);
        }

        // TODO: 检查是否有用户关联

        deptManager.removeById(id);
        log.info("删除部门成功: id={}", id);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        DeptEntity dept = deptManager.getById(id);
        if (dept == null) {
            throw new BusinessException(DeptErrorCode.DEPT_NOT_EXISTS);
        }
        DeptEntity entity = new DeptEntity();
        entity.setId(id);
        entity.setStatus(status);
        deptManager.updateById(entity);
        log.info("更新部门状态: id={}, status={}", id, status);
    }

    /**
     * Entity → DeptVO
     */
    private DeptVO toDeptVO(DeptEntity entity) {
        return DeptVO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .parentId(entity.getParentId())
                .sort(entity.getSort())
                .leaderUserId(entity.getLeaderUserId())
                .phone(entity.getPhone())
                .email(entity.getEmail())
                .status(entity.getStatus())
                .remark(entity.getRemark())
                .createTime(entity.getCreateTime())
                .build();
    }

    /**
     * 构建部门树
     */
    private List<DeptTreeVO> buildTree(List<DeptEntity> allDepts, Long parentId) {
        return allDepts.stream()
                .filter(d -> parentId.equals(d.getParentId()))
                .map(d -> DeptTreeVO.builder()
                        .id(d.getId())
                        .name(d.getName())
                        .parentId(d.getParentId())
                        .sort(d.getSort())
                        .leaderUserId(d.getLeaderUserId())
                        .phone(d.getPhone())
                        .email(d.getEmail())
                        .status(d.getStatus())
                        .remark(d.getRemark())
                        .createTime(d.getCreateTime())
                        .children(buildTree(allDepts, d.getId()))
                        .build())
                .collect(Collectors.toList());
    }
}

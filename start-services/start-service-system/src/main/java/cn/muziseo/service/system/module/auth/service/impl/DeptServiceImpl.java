package cn.muziseo.service.system.module.auth.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.muziseo.service.system.module.auth.controller.request.DeptAddRequest;
import cn.muziseo.service.system.module.auth.manager.DeptManager;
import cn.muziseo.service.system.module.auth.repository.entity.DeptEntity;
import cn.muziseo.service.system.module.auth.service.DeptService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 部门业务实现
 * <p>
 * 实现部门的增删改查、树形结构查询等功能
 *
 * @author 木子软件
 * @Date 2026-02-11
 * @Wechat liiayy
 * @Email 773582348@qq.com
 * @Copyright <a href="https://code.muziseo.cn">木子软件</a>
 */
@Service
@Slf4j
public class DeptServiceImpl implements DeptService {

    @Resource
    private DeptManager deptManager;

    @Override
    public List<DeptEntity> list() {
        // 调用Manager层查询
        return deptManager.listAll();
    }

    @Override
    public List<DeptEntity> tree() {
        // 调用Manager层查询树形结构
        return deptManager.tree();
    }

    @Override
    public DeptEntity getById(Long id) {
        return deptManager.getById(id);
    }

    @Override
    public void addDept(DeptAddRequest request) {
        log.info("新增部门: name={}", request.getName());
        DeptEntity deptEntity = BeanUtil.copyProperties(request, DeptEntity.class);
        if (deptEntity.getParentId() == null) {
            deptEntity.setParentId(0L);
        }
        if (deptEntity.getStatus() == null) {
            deptEntity.setStatus(0);
        }
        deptManager.save(deptEntity);
        log.info("新增部门成功: id={}, name={}", deptEntity.getId(), deptEntity.getName());
    }

    @Override
    public void updateDept(Long id, DeptAddRequest request) {
        log.info("更新部门: id={}", id);
        DeptEntity deptEntity = BeanUtil.copyProperties(request, DeptEntity.class);
        deptEntity.setId(id);
        deptManager.updateById(deptEntity);
        log.info("更新部门成功: id={}", id);
    }

    @Override
    public void deleteDept(Long id) {
        log.info("删除部门: id={}", id);

        // 调用Manager层检查是否有子部门
        long count = deptManager.countByParentId(id);
        if (count > 0) {
            throw new RuntimeException("存在子部门，无法删除");
        }

        // 检查是否有用户关联
        // TODO: 添加用户检查逻辑

        deptManager.removeById(id);
        log.info("删除部门成功: id={}", id);
    }
}

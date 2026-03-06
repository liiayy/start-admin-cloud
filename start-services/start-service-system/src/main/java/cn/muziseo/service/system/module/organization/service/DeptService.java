package cn.muziseo.service.system.module.organization.service;

import cn.muziseo.service.system.module.organization.controller.request.DeptAddRequest;
import cn.muziseo.service.system.module.organization.repository.entity.DeptEntity;

import java.util.List;

/**
 * 部门业务接口
 * <p>
 * 提供部门的增删改查、树形结构查询等功能
 *
 * @author 木子软件
 * @Date 2026-02-11
 * @Wechat liiayy
 * @Email 773582348@qq.com
 * @Copyright <a href="https://code.muziseo.cn">木子软件</a>
 */
public interface DeptService {

    /**
     * 获取所有部门列表
     *
     * @return 部门列表
     */
    List<DeptEntity> list();

    /**
     * 获取部门树形结构
     *
     * @return 部门树
     */
    List<DeptEntity> tree();

    /**
     * 根据ID获取部门
     *
     * @param id 部门ID
     * @return 部门实体
     */
    DeptEntity getById(Long id);

    /**
     * 添加部门
     *
     * @param request 添加请求
     */
    void addDept(DeptAddRequest request);

    /**
     * 更新部门
     *
     * @param id      部门ID
     * @param request 更新请求
     */
    void updateDept(Long id, DeptAddRequest request);

    /**
     * 删除部门
     *
     * @param id 部门ID
     */
    void deleteDept(Long id);
}

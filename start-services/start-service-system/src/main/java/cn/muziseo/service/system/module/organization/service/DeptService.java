package cn.muziseo.service.system.module.organization.service;

import cn.muziseo.service.system.module.organization.controller.request.DeptCreateRequest;
import cn.muziseo.service.system.module.organization.controller.vo.DeptTreeVO;
import cn.muziseo.service.system.module.organization.controller.vo.DeptVO;

import java.util.List;

/**
 * 部门业务接口
 *
 * @author 木子软件
 */
public interface DeptService {

    List<DeptVO> list();

    List<DeptTreeVO> tree();

    DeptVO getById(Long id);

    void createDept(DeptCreateRequest request);

    void updateDept(Long id, DeptCreateRequest request);

    void deleteDept(Long id);

    void updateStatus(Long id, Integer status);
}

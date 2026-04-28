package cn.muziseo.service.system.module.organization.api;

import cn.hutool.core.bean.BeanUtil;
import cn.muziseo.service.system.module.organization.api.dto.DeptRemoteDTO;
import cn.muziseo.service.system.module.organization.manager.DeptManager;
import cn.muziseo.service.system.module.organization.repository.entity.DeptEntity;
import cn.muziseo.service.system.module.organization.convert.DeptConverter;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 部门 RPC 接口实现
 *
 * @author 木子软件
 */
@RestController
public class DeptApiImpl implements DeptApi {

    @Resource
    private DeptManager deptManager;

    @Resource
    private DeptConverter deptConverter;

    @Override
    public DeptRemoteDTO getDeptById(Long id) {
        DeptEntity dept = deptManager.getById(id);
        return dept != null ? deptConverter.toRemoteDTO(dept) : null;
    }

    @Override
    public List<DeptRemoteDTO> listDeptsByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        List<DeptEntity> depts = deptManager.listByIds(ids);
        return depts.stream()
                .map(deptConverter::toRemoteDTO)
                .collect(Collectors.toList());
    }
}

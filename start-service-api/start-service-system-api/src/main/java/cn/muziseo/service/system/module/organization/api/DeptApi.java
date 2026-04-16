package cn.muziseo.service.system.module.organization.api;

import cn.muziseo.service.system.constants.ApiConstants;
import cn.muziseo.service.system.module.organization.api.dto.DeptRemoteDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 部门 RPC 接口
 *
 * @author 木子软件
 */
@FeignClient(name = ApiConstants.NAME, contextId = "deptApi")
public interface DeptApi {

    String PREFIX = ApiConstants.PREFIX + "/dept";

    /**
     * 根据 ID 获取部门信息
     *
     * @param id 部门ID
     * @return 部门信息
     */
    @GetMapping(PREFIX + "/get-by-id")
    DeptRemoteDTO getDeptById(@RequestParam("id") Long id);

    /**
     * 根据 ID 列表批量获取部门信息
     *
     * @param ids 部门ID列表
     * @return 部门列表
     */
    @GetMapping(PREFIX + "/list-by-ids")
    List<DeptRemoteDTO> listDeptsByIds(@RequestParam("ids") List<Long> ids);
}

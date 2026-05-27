package cn.muziseo.service.system.module.auth.api;

import cn.muziseo.service.system.constants.ApiConstants;
import cn.muziseo.service.system.module.auth.api.dto.UserRemoteDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 用户 RPC 接口
 *
 * @author 木子软件
 */
@FeignClient(name = ApiConstants.NAME, contextId = "userApi")
public interface UserApi {

    String PREFIX = ApiConstants.PREFIX + "/user";

    /**
     * 根据 ID 获取用户信息
     *
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping(PREFIX + "/get-by-id")
    UserRemoteDTO getUserById(@RequestParam("id") Long id);

    /**
     * 根据 ID 列表批量获取用户信息
     *
     * @param ids 用户ID列表
     * @return 用户列表
     */
    @GetMapping(PREFIX + "/list-by-ids")
    List<UserRemoteDTO> listUsersByIds(@RequestParam("ids") List<Long> ids);

    /**
     * 根据用户名获取用户信息
     *
     * @param username 用户名
     * @return 用户信息
     */
    @GetMapping(PREFIX + "/get-by-username")
    UserRemoteDTO getUserByUsername(@RequestParam("username") String username);

    /**
     * 更新用户昵称
     *
     * @param id 用户ID
     * @param nickname 昵称
     */
    @PostMapping(PREFIX + "/update-nickname")
    void updateNickname(@RequestParam("id") Long id, @RequestParam("nickname") String nickname);
}

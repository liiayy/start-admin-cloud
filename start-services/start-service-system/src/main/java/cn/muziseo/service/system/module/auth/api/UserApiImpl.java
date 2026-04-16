package cn.muziseo.service.system.module.auth.api;

import cn.hutool.core.bean.BeanUtil;
import cn.muziseo.service.system.module.auth.api.dto.UserRemoteDTO;
import cn.muziseo.service.system.module.auth.manager.UserManager;
import cn.muziseo.service.system.module.auth.repository.entity.UserEntity;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户 RPC 接口实现
 *
 * @author 木子软件
 */
@RestController
public class UserApiImpl implements UserApi {

    @Resource
    private UserManager userManager;

    @Override
    public UserRemoteDTO getUserById(Long id) {
        UserEntity user = userManager.getById(id);
        return user != null ? BeanUtil.copyProperties(user, UserRemoteDTO.class) : null;
    }

    @Override
    public List<UserRemoteDTO> listUsersByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        List<UserEntity> users = userManager.listByIds(ids);
        return users.stream()
                .map(user -> BeanUtil.copyProperties(user, UserRemoteDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public UserRemoteDTO getUserByUsername(String username) {
        UserEntity user = userManager.getByUsername(username);
        return user != null ? BeanUtil.copyProperties(user, UserRemoteDTO.class) : null;
    }
}

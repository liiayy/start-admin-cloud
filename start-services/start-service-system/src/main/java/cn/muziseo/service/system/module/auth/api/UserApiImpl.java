package cn.muziseo.service.system.module.auth.api;

import cn.hutool.core.bean.BeanUtil;
import cn.muziseo.service.system.module.auth.api.dto.UserRemoteDTO;
import cn.muziseo.service.system.module.auth.manager.UserManager;
import cn.muziseo.service.system.module.auth.repository.entity.UserEntity;
import cn.muziseo.service.system.module.auth.convert.UserConverter;
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

    @Resource
    private UserConverter userConverter;

    @Override
    public UserRemoteDTO getUserById(Long id) {
        UserEntity user = userManager.getById(id);
        return user != null ? userConverter.toRemoteDTO(user) : null;
    }

    @Override
    public List<UserRemoteDTO> listUsersByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        List<UserEntity> users = userManager.listByIds(ids);
        return users.stream()
                .map(userConverter::toRemoteDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserRemoteDTO getUserByUsername(String username) {
        UserEntity user = userManager.getByUsername(username);
        return user != null ? userConverter.toRemoteDTO(user) : null;
    }
}

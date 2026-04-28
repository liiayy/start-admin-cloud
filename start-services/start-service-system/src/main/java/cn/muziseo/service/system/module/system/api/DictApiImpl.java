package cn.muziseo.service.system.module.system.api;

import cn.muziseo.common.core.domain.dto.DictDataSimpleDTO;
import cn.muziseo.service.system.module.dict.api.DictApi;
import cn.muziseo.service.system.module.system.service.DictService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 字典数据 RPC 接口实现
 * <p>
 * 直接实现 {@link DictApi} Feign 接口，编译期保证契约一致性。
 * 路由路径完全继承自接口定义，无需重复声明。
 * </p>
 *
 * @author 木子软件
 */
@RestController
public class DictApiImpl implements DictApi {

    @Resource
    private DictService dictService;

    @Override
    public List<DictDataSimpleDTO> listByType(String dictType) {
        return dictService.listSimpleByDictType(dictType);
    }
}

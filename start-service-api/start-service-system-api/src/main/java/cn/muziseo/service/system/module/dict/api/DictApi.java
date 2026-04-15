package cn.muziseo.service.system.module.dict.api;

import cn.muziseo.common.core.domain.dto.DictDataSimpleDTO;
import cn.muziseo.service.system.constants.ApiConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 字典数据 RPC 接口（供其他微服务远程调用）
 *
 * <p>
 * 该接口由 system 服务实现，其他业务微服务通过 OpenFeign 调用，
 * 配合 {@link cn.muziseo.common.cache.dict.DictCacheManager} 二级缓存使用。
 * </p>
 *
 * @author 木子软件
 */
@FeignClient(name = ApiConstants.NAME, contextId = "dictApi")
public interface DictApi {

    String PREFIX = ApiConstants.PREFIX + "/dict";

    /**
     * 根据字典类型编码获取字典数据列表
     *
     * @param dictType 字典类型编码
     * @return 字典数据列表（简版 DTO）
     */
    @GetMapping(PREFIX + "/list-by-type")
    List<DictDataSimpleDTO> listByType(@RequestParam("dictType") String dictType);
}

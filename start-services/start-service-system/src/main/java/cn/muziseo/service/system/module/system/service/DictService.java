package cn.muziseo.service.system.module.system.service;

import cn.muziseo.common.core.domain.dto.DictDataSimpleDTO;
import cn.muziseo.common.db.page.PageResponse;
import cn.muziseo.service.system.module.system.controller.request.DictDataCreateRequest;
import cn.muziseo.service.system.module.system.controller.request.DictDataPageRequest;
import cn.muziseo.service.system.module.system.controller.vo.DictDataVO;

import java.util.List;

/**
 * 字典数据业务接口
 *
 * @author 木子软件
 * @Date 2026-02-27
 */
public interface DictService {

    /**
     * 根据字典类型编码获取字典数据列表（前端页面管理用，包含全部字段）
     */
    List<DictDataVO> listByDictType(String dictType);

    /**
     * 根据字典类型编码获取精简字典数据列表（RPC 传输 & 缓存用）
     */
    List<DictDataSimpleDTO> listSimpleByDictType(String dictType);

    /**
     * 分页查询字典数据
     */
    PageResponse<DictDataVO> pageDictData(DictDataPageRequest request);

    /**
     * 根据ID获取字典数据
     */
    DictDataVO getDictDataById(Long id);

    /**
     * 添加字典数据
     */
    void createDictData(DictDataCreateRequest request);

    /**
     * 更新字典数据
     */
    void updateDictData(Long id, DictDataCreateRequest request);

    /**
     * 删除字典数据
     */
    void deleteDictData(Long id);
}

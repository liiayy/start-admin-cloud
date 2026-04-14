package cn.muziseo.service.system.module.system.service;

import cn.muziseo.common.db.page.PageResponse;
import cn.muziseo.service.system.module.system.controller.request.DictDataAddRequest;
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
     * 根据字典类型编码获取字典数据列表
     */
    List<DictDataVO> listByDictType(String dictType);

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
    void addDictData(DictDataAddRequest request);

    /**
     * 更新字典数据
     */
    void updateDictData(Long id, DictDataAddRequest request);

    /**
     * 删除字典数据
     */
    void deleteDictData(Long id);
}

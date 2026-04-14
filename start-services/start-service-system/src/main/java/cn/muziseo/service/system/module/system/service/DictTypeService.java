package cn.muziseo.service.system.module.system.service;

import cn.muziseo.common.db.page.PageResponse;
import cn.muziseo.service.system.module.system.controller.request.DictTypeAddRequest;
import cn.muziseo.service.system.module.system.controller.request.DictTypePageRequest;
import cn.muziseo.service.system.module.system.controller.vo.DictTypeVO;

import java.util.List;

/**
 * 字典类型业务接口
 *
 * @author 木子软件
 * @Date 2026-02-27
 */
public interface DictTypeService {

    /**
     * 获取所有字典类型列表
     */
    List<DictTypeVO> list();

    /**
     * 分页查询字典类型
     */
    PageResponse<DictTypeVO> pageDictType(DictTypePageRequest request);

    /**
     * 根据ID获取字典类型
     */
    DictTypeVO getDictTypeById(Long id);

    /**
     * 添加字典类型
     */
    void addDictType(DictTypeAddRequest request);

    /**
     * 更新字典类型
     */
    void updateDictType(Long id, DictTypeAddRequest request);

    /**
     * 删除字典类型
     */
    void deleteDictType(Long id);
}

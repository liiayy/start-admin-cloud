package cn.muziseo.service.system.module.system.service;

import cn.muziseo.service.system.module.system.controller.request.DictAddRequest;
import cn.muziseo.service.system.module.system.repository.entity.DictEntity;

import java.util.List;

/**
 * 字典数据业务接口
 *
 * @author 木子软件
 * @Date 2026-02-27
 */
public interface DictService {

    /**
     * 获取所有字典数据列表
     */
    List<DictEntity> list();

    /**
     * 根据字典类型编码获取字典数据列表
     */
    List<DictEntity> listByDictTypeCode(String dictTypeCode);

    /**
     * 根据ID获取字典数据
     */
    DictEntity getById(Long id);

    /**
     * 添加字典数据
     */
    void addDict(DictAddRequest request);

    /**
     * 更新字典数据
     */
    void updateDict(Long id, DictAddRequest request);

    /**
     * 删除字典数据
     */
    void deleteDict(Long id);
}

package cn.muziseo.service.system.module.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.muziseo.common.core.exception.BusinessException;
import cn.muziseo.service.system.enums.DictErrorCode;
import cn.muziseo.service.system.module.system.controller.request.DictAddRequest;
import cn.muziseo.service.system.module.system.manager.DictManager;
import cn.muziseo.service.system.module.system.manager.DictTypeManager;
import cn.muziseo.service.system.module.system.repository.entity.DictEntity;
import cn.muziseo.service.system.module.system.service.DictService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 字典数据业务实现
 *
 * @author 木子软件
 * @Date 2026-02-27
 */
@Service
@Slf4j
public class DictServiceImpl implements DictService {

    @Resource
    private DictManager dictManager;

    @Resource
    private DictTypeManager dictTypeManager;

    @Override
    public List<DictEntity> list() {
        return dictManager.list();
    }

    @Override
    public List<DictEntity> listByDictTypeCode(String dictTypeCode) {
        return dictManager.listByDictTypeCode(dictTypeCode);
    }

    @Override
    public DictEntity getById(Long id) {
        return dictManager.getById(id);
    }

    @Override
    public void addDict(DictAddRequest request) {
        log.info("新增字典数据: dictTypeCode={}, label={}", request.getDictTypeCode(), request.getLabel());

        // 检查字典类型是否存在
        if (dictTypeManager.getByCode(request.getDictTypeCode()) == null) {
            throw new BusinessException(DictErrorCode.DICT_TYPE_NOT_EXISTS);
        }

        DictEntity entity = BeanUtil.copyProperties(request, DictEntity.class);
        if (entity.getStatus() == null) {
            entity.setStatus(0);
        }
        dictManager.save(entity);
        log.info("新增字典数据成功: id={}", entity.getId());
    }

    @Override
    public void updateDict(Long id, DictAddRequest request) {
        log.info("更新字典数据: id={}", id);

        DictEntity existing = dictManager.getById(id);
        if (existing == null) {
            throw new BusinessException(DictErrorCode.DICT_DATA_NOT_EXISTS);
        }

        DictEntity entity = BeanUtil.copyProperties(request, DictEntity.class);
        entity.setId(id);
        dictManager.updateById(entity);
        log.info("更新字典数据成功: id={}", id);
    }

    @Override
    public void deleteDict(Long id) {
        log.info("删除字典数据: id={}", id);

        DictEntity existing = dictManager.getById(id);
        if (existing == null) {
            throw new BusinessException(DictErrorCode.DICT_DATA_NOT_EXISTS);
        }

        dictManager.removeById(id);
        log.info("删除字典数据成功: id={}", id);
    }
}

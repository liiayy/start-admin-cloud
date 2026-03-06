package cn.muziseo.service.system.module.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.muziseo.service.system.module.system.controller.request.DictTypeAddRequest;
import cn.muziseo.service.system.module.system.manager.DictManager;
import cn.muziseo.service.system.module.system.manager.DictTypeManager;
import cn.muziseo.service.system.module.system.repository.entity.DictTypeEntity;
import cn.muziseo.service.system.module.system.service.DictTypeService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 字典类型业务实现
 *
 * @author 木子软件
 * @Date 2026-02-27
 */
@Service
@Slf4j
public class DictTypeServiceImpl implements DictTypeService {

    @Resource
    private DictTypeManager dictTypeManager;

    @Resource
    private DictManager dictManager;

    @Override
    public List<DictTypeEntity> list() {
        return dictTypeManager.listAll();
    }

    @Override
    public DictTypeEntity getById(Long id) {
        return dictTypeManager.getById(id);
    }

    @Override
    public void addDictType(DictTypeAddRequest request) {
        log.info("新增字典类型: code={}, name={}", request.getCode(), request.getName());

        // 检查字典类型编码是否存在
        if (dictTypeManager.existsByCode(request.getCode())) {
            throw new RuntimeException("字典类型编码已存在");
        }

        DictTypeEntity entity = BeanUtil.copyProperties(request, DictTypeEntity.class);
        if (entity.getStatus() == null) {
            entity.setStatus(0);
        }
        dictTypeManager.save(entity);
        log.info("新增字典类型成功: code={}", entity.getCode());
    }

    @Override
    public void updateDictType(Long id, DictTypeAddRequest request) {
        log.info("更新字典类型: id={}", id);

        DictTypeEntity existing = dictTypeManager.getById(id);
        if (existing == null) {
            throw new RuntimeException("字典类型不存在");
        }

        DictTypeEntity entity = BeanUtil.copyProperties(request, DictTypeEntity.class);
        entity.setId(id);
        dictTypeManager.updateById(entity);
        log.info("更新字典类型成功: id={}", id);
    }

    @Override
    public void deleteDictType(Long id) {
        log.info("删除字典类型: id={}", id);

        DictTypeEntity dictType = dictTypeManager.getById(id);
        if (dictType == null) {
            throw new RuntimeException("字典类型不存在");
        }

        // 检查是否有字典数据
        long count = dictManager.listByDictTypeCode(dictType.getCode()).size();
        if (count > 0) {
            throw new RuntimeException("该字典类型下存在字典数据，无法删除");
        }

        dictTypeManager.removeById(id);
        log.info("删除字典类型成功: id={}", id);
    }
}

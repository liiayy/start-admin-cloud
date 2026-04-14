package cn.muziseo.service.system.module.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.muziseo.common.core.exception.BusinessException;
import cn.muziseo.common.db.page.PageResponse;
import cn.muziseo.service.system.enums.DictErrorCode;
import cn.muziseo.service.system.module.system.controller.request.DictTypeAddRequest;
import cn.muziseo.service.system.module.system.controller.request.DictTypePageRequest;
import cn.muziseo.service.system.module.system.controller.vo.DictTypeVO;
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
    public List<DictTypeVO> list() {
        return dictTypeManager.listAll().stream()
                .map(this::toDictTypeVO)
                .toList();
    }

    @Override
    public PageResponse<DictTypeVO> pageDictType(DictTypePageRequest request) {
        var page = dictTypeManager.pageDictType(request);
        List<DictTypeVO> voList = page.getRecords().stream()
                .map(this::toDictTypeVO)
                .toList();
        return new PageResponse<>(voList, (int) page.getTotalRow());
    }

    @Override
    public DictTypeVO getDictTypeById(Long id) {
        DictTypeEntity entity = dictTypeManager.getById(id);
        return toDictTypeVO(entity);
    }

    @Override
    public void addDictType(DictTypeAddRequest request) {
        // 检查字典类型是否已存在
        if (dictTypeManager.existsByType(request.getType())) {
            throw new BusinessException(DictErrorCode.DICT_TYPE_CODE_EXISTS);
        }

        DictTypeEntity entity = BeanUtil.copyProperties(request, DictTypeEntity.class);
        if (entity.getStatus() == null) {
            entity.setStatus(0);
        }
        dictTypeManager.save(entity);
    }

    @Override
    public void updateDictType(Long id, DictTypeAddRequest request) {
        DictTypeEntity existing = dictTypeManager.getById(id);
        if (existing == null) {
            throw new BusinessException(DictErrorCode.DICT_TYPE_NOT_EXISTS);
        }

        // 如果类型编码变更，检查新编码是否已被占用
        if (!existing.getType().equals(request.getType())
                && dictTypeManager.existsByType(request.getType())) {
            throw new BusinessException(DictErrorCode.DICT_TYPE_CODE_EXISTS);
        }

        DictTypeEntity entity = BeanUtil.copyProperties(request, DictTypeEntity.class);
        entity.setId(id);
        dictTypeManager.updateById(entity);
    }

    @Override
    public void deleteDictType(Long id) {
        DictTypeEntity dictType = dictTypeManager.getById(id);
        if (dictType == null) {
            throw new BusinessException(DictErrorCode.DICT_TYPE_NOT_EXISTS);
        }

        // 检查是否有字典数据
        long count = dictManager.listByDictType(dictType.getType()).size();
        if (count > 0) {
            throw new BusinessException(DictErrorCode.DICT_TYPE_HAS_DATA);
        }

        dictTypeManager.removeById(id);
    }

    /**
     * Entity 转 VO
     */
    private DictTypeVO toDictTypeVO(DictTypeEntity entity) {
        if (entity == null) {
            return null;
        }
        return DictTypeVO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .type(entity.getType())
                .status(entity.getStatus())
                .remark(entity.getRemark())
                .createTime(entity.getCreateTime())
                .build();
    }
}

package cn.muziseo.service.system.module.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.muziseo.common.core.exception.BusinessException;
import cn.muziseo.common.db.page.PageResponse;
import cn.muziseo.service.system.enums.DictErrorCode;
import cn.muziseo.service.system.module.system.controller.request.DictDataAddRequest;
import cn.muziseo.service.system.module.system.controller.request.DictDataPageRequest;
import cn.muziseo.service.system.module.system.controller.vo.DictDataVO;
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
    public List<DictDataVO> listByDictType(String dictType) {
        return dictManager.listByDictType(dictType).stream()
                .map(this::toDictDataVO)
                .toList();
    }

    @Override
    public PageResponse<DictDataVO> pageDictData(DictDataPageRequest request) {
        var page = dictManager.pageDictData(request);
        List<DictDataVO> voList = page.getRecords().stream()
                .map(this::toDictDataVO)
                .toList();
        return new PageResponse<>(voList, (int) page.getTotalRow());
    }

    @Override
    public DictDataVO getDictDataById(Long id) {
        DictEntity entity = dictManager.getById(id);
        return toDictDataVO(entity);
    }

    @Override
    public void addDictData(DictDataAddRequest request) {
        // 检查字典类型是否存在
        if (dictTypeManager.getByType(request.getDictType()) == null) {
            throw new BusinessException(DictErrorCode.DICT_TYPE_NOT_EXISTS);
        }

        DictEntity entity = BeanUtil.copyProperties(request, DictEntity.class);
        if (entity.getStatus() == null) {
            entity.setStatus(0);
        }
        dictManager.save(entity);
    }

    @Override
    public void updateDictData(Long id, DictDataAddRequest request) {
        DictEntity existing = dictManager.getById(id);
        if (existing == null) {
            throw new BusinessException(DictErrorCode.DICT_DATA_NOT_EXISTS);
        }

        DictEntity entity = BeanUtil.copyProperties(request, DictEntity.class);
        entity.setId(id);
        dictManager.updateById(entity);
    }

    @Override
    public void deleteDictData(Long id) {
        DictEntity existing = dictManager.getById(id);
        if (existing == null) {
            throw new BusinessException(DictErrorCode.DICT_DATA_NOT_EXISTS);
        }

        dictManager.removeById(id);
    }

    /**
     * Entity 转 VO
     */
    private DictDataVO toDictDataVO(DictEntity entity) {
        if (entity == null) {
            return null;
        }
        return DictDataVO.builder()
                .id(entity.getId())
                .dictType(entity.getDictType())
                .label(entity.getLabel())
                .value(entity.getValue())
                .sort(entity.getSort())
                .status(entity.getStatus())
                .colorType(entity.getColorType())
                .cssClass(entity.getCssClass())
                .remark(entity.getRemark())
                .createTime(entity.getCreateTime())
                .build();
    }
}

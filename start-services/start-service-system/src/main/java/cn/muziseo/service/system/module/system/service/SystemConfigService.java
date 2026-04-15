package cn.muziseo.service.system.module.system.service;

import cn.muziseo.common.db.page.PageResponse;
import cn.muziseo.service.system.module.system.controller.request.SystemConfigAddRequest;
import cn.muziseo.service.system.module.system.controller.request.SystemConfigPageRequest;
import cn.muziseo.service.system.module.system.controller.vo.SystemConfigVO;

/**
 * 系统参数业务接口
 *
 * @author 木子软件
 * @Date 2026-02-27
 */
public interface SystemConfigService {

    /**
     * 分页查询系统参数
     */
    PageResponse<SystemConfigVO> pageConfig(SystemConfigPageRequest request);

    /**
     * 根据ID获取系统参数
     */
    SystemConfigVO getConfigById(Long id);

    /**
     * 根据参数键名获取参数键值
     */
    String getConfigValue(String configKey);

    /**
     * 添加系统参数
     */
    void addConfig(SystemConfigAddRequest request);

    /**
     * 更新系统参数
     */
    void updateConfig(Long id, SystemConfigAddRequest request);

    /**
     * 删除系统参数
     */
    void deleteConfig(Long id);
}

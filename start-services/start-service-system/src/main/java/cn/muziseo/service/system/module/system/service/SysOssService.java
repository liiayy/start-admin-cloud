package cn.muziseo.service.system.module.system.service;

import cn.muziseo.service.system.module.system.repository.entity.SysOssEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * OSS 文件业务接口
 *
 * @author 木子软件
 */
public interface SysOssService {

    /**
     * 上传文件
     *
     * @param file       文件
     * @param moduleName 模块名
     * @return 存储实体
     */
    SysOssEntity upload(MultipartFile file, String moduleName);

    /**
     * 删除文件
     *
     * @param id 资源ID
     */
    void delete(Long id);

    /**
     * 根据 ID 获取文件记录
     *
     * @param id 资源ID
     * @return 存储实体
     */
    SysOssEntity getById(Long id);

    /**
     * 根据 ID 列表批量获取文件记录
     *
     * @param ids 资源ID列表
     * @return 存储实体列表
     */
    List<SysOssEntity> listByIds(List<Long> ids);
}

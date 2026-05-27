package cn.muziseo.service.system.module.system.api;

import cn.muziseo.service.system.module.system.api.dto.FileRemoteDTO;
import cn.muziseo.service.system.module.system.repository.entity.SysOssEntity;
import cn.muziseo.service.system.module.system.service.SysOssService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件远程调用 RPC 接口实现
 *
 * @author 木子软件
 */
@RestController
public class FileApiImpl implements FileApi {

    @Resource
    private SysOssService sysOssService;

    @Override
    public FileRemoteDTO uploadFile(MultipartFile file) {
        // 调用底层的 OSS 存储服务，模块分类存入 "rpc"
        SysOssEntity oss = sysOssService.upload(file, "rpc");
        if (oss == null) {
            return null;
        }

        // 构造返回的远程数据 DTO 对象
        return FileRemoteDTO.builder()
                .id(oss.getId())
                .url(oss.getUrl())
                .fileName(oss.getFileName())
                .originalName(oss.getOriginalName())
                .fileSuffix(oss.getFileSuffix())
                .fileSize(oss.getSize())
                .build();
    }
}

package cn.muziseo.service.system.module.system.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.muziseo.common.core.exception.BusinessException;
import cn.muziseo.common.oss.config.OssConfigProperties;
import cn.muziseo.common.oss.core.OssClient;
import cn.muziseo.common.oss.entity.UploadResult;
import cn.muziseo.common.oss.factory.OssFactory;
import cn.muziseo.service.system.enums.OssErrorCode;
import cn.muziseo.service.system.module.system.manager.SysOssConfigManager;
import cn.muziseo.service.system.module.system.manager.SysOssManager;
import cn.muziseo.service.system.module.system.repository.entity.SysOssConfigEntity;
import cn.muziseo.service.system.module.system.repository.entity.SysOssEntity;
import cn.muziseo.service.system.module.system.service.SysOssService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * OSS 文件业务实现
 *
 * @author 木子软件
 */
@Service
@Slf4j
public class SysOssServiceImpl implements SysOssService {

    @Resource
    private SysOssManager sysOssManager;

    @Resource
    private SysOssConfigManager sysOssConfigManager;

    @Resource
    private OssConfigProperties ossConfigProperties;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysOssEntity upload(MultipartFile file, String moduleName) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(OssErrorCode.UPLOAD_FILE_EMPTY);
        }

        // 1. 安全校验：大小校验 (从配置获取)
        if (file.getSize() > ossConfigProperties.getMaxSize()) {
            double mbSize = (double) ossConfigProperties.getMaxSize() / (1024 * 1024);
            throw new BusinessException("上传失败：文件大小超过 " + mbSize + "MB 限制");
        }

        // 2. 安全校验：模块名合法性
        if (moduleName != null && !moduleName.matches("^[a-zA-Z0-9_-]+$")) {
            throw new BusinessException(OssErrorCode.UPLOAD_MODULE_INVALID);
        }

        // 3. 安全校验：后缀校验 (从配置获取)
        String originalName = file.getOriginalFilename();
        String suffix = FileUtil.extName(originalName).toLowerCase();
        if (ossConfigProperties.getForbiddenSuffix().contains(suffix)) {
            throw new BusinessException("上传失败：禁止上传该类型文件");
        }

        // 4. 获取当前主存储配置
        List<SysOssConfigEntity> configs = sysOssConfigManager.listEnabledConfig();
        if (configs.isEmpty()) {
            throw new BusinessException(OssErrorCode.OSS_CLIENT_NOT_FOUND);
        }
        SysOssConfigEntity config = configs.get(0);

        try {
            // 5. 获取具体存储客户端
            OssClient storage = OssFactory.instance(config.getConfigKey());
            
            // 6. 执行物理上传 (带上 Content-Type，让云端正确识别文件类型)
            byte[] data = file.getBytes();
            UploadResult uploadResult = storage.uploadSuffix(data, suffix, moduleName, file.getContentType());
            
            // 7. 保存元数据到数据库
            SysOssEntity oss = new SysOssEntity();
            oss.setFileName(uploadResult.getFileName());
            oss.setOriginalName(originalName);
            oss.setFileSuffix(suffix);
            oss.setUrl(uploadResult.getUrl());
            oss.setSize(file.getSize());
            oss.setService(config.getConfigKey());
            oss.setContentType(file.getContentType());
            oss.setMd5(DigestUtil.md5Hex(data));
            
            sysOssManager.save(oss);
            return oss;
        } catch (Exception e) {
            log.error("文件上传异常: {}", e.getMessage(), e);
            throw new BusinessException("文件上传失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        SysOssEntity oss = sysOssManager.getById(id);
        if (oss == null) {
            return;
        }

        // 1. 删除数据库记录
        sysOssManager.removeById(id);

        // 2. 尝试删除物理文件
        try {
            OssClient storage = OssFactory.instance(oss.getService());
            storage.delete(oss.getFileName());
            log.info("删除物理文件成功: id={}, key={}", id, oss.getFileName());
        } catch (Exception e) {
            log.warn("物理文件删除失败 (可能文件已不存在): id={}, error={}", id, e.getMessage());
        }
    }

    @Override
    public SysOssEntity getById(Long id) {
        return sysOssManager.getById(id);
    }

    @Override
    public List<SysOssEntity> listByIds(List<Long> ids) {
        return sysOssManager.listByIds(ids);
    }
}

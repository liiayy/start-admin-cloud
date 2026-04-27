package cn.muziseo.service.system.module.system.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
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

        // 1. 安全校验：大小校验
        if (file.getSize() > ossConfigProperties.getMaxSize()) {
            double mbSize = (double) ossConfigProperties.getMaxSize() / (1024 * 1024);
            throw new BusinessException("上传失败：文件大小超过 " + String.format("%.1f", mbSize) + "MB 限制");
        }

        // 2. 安全校验：后缀校验
        String originalName = file.getOriginalFilename();
        String suffix = FileUtil.extName(originalName).toLowerCase();
        if (ossConfigProperties.getForbiddenSuffix().contains(suffix)) {
            throw new BusinessException("上传失败：禁止上传该类型文件");
        }

        // 3. 获取存储配置
        List<SysOssConfigEntity> configs = sysOssConfigManager.listEnabledConfig();
        if (configs.isEmpty()) {
            throw new BusinessException(OssErrorCode.OSS_CLIENT_NOT_FOUND);
        }
        SysOssConfigEntity config = configs.get(0);

        UploadResult uploadResult = null;
        OssClient storage = null;
        try {
            byte[] data = file.getBytes();
            
            // 【极致加固】安全校验：基于魔数（Magic Number）验证真实文件类型
            // 防止恶意用户通过修改后缀（如 .php -> .jpg）绕过校验
            String realType = cn.hutool.core.io.FileTypeUtil.getType(new java.io.ByteArrayInputStream(data));
            
            // 1. 拦截已知危险类型
            if (StrUtil.isNotBlank(realType) && ossConfigProperties.getForbiddenSuffix().contains(realType.toLowerCase())) {
                log.warn("[安全拦截] 检测到文件后缀与实际内容不符的风险上传: filename={}, realType={}", originalName, realType);
                throw new BusinessException("上传失败：文件内容与后缀不符，疑似非法文件");
            }
            
            // 2. 深度加固：如果后缀是图片，但魔数识别不是图片（且非空），则拦截
            List<String> imageSuffixes = List.of("jpg", "jpeg", "png", "gif", "bmp", "webp");
            if (imageSuffixes.contains(suffix) && StrUtil.isNotBlank(realType) && !imageSuffixes.contains(realType.toLowerCase())) {
                log.warn("[安全拦截] 检测到图片伪装风险: filename={}, realType={}", originalName, realType);
                throw new BusinessException("上传失败：图片格式非法或已被篡改");
            }

            storage = OssFactory.instance(config.getConfigKey());
            
            // 4. 执行物理上传
            uploadResult = storage.uploadSuffix(data, suffix, moduleName, file.getContentType());
            
            // 5. 保存元数据
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
            // [极致加固] 补偿机制：如果上传成功但后续（如存库）失败，尝试清理云端脏数据
            if (uploadResult != null && storage != null) {
                try {
                    storage.delete(uploadResult.getFileName());
                    log.info("上传失败，已自动清理云端脏数据: {}", uploadResult.getFileName());
                } catch (Exception ex) {
                    log.warn("清理云端脏数据失败: {}", ex.getMessage());
                }
            }
            log.error("文件上传异常: {}", e.getMessage());
            if (e instanceof BusinessException) {
                throw (BusinessException) e;
            }
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

        // [安全加固] 权限校验 (如果以后有租户或多用户系统，这里应校验 oss.getCreator())
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

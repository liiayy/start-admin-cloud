package cn.muziseo.service.system.module.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.core.io.FileUtil;
import cn.muziseo.common.core.domain.dto.ResponseDTO;
import cn.muziseo.common.log.annotation.Log;
import cn.muziseo.common.log.enums.BusinessType;
import cn.muziseo.service.system.module.system.controller.vo.SysOssVO;
import cn.muziseo.service.system.module.system.repository.entity.SysOssEntity;
import cn.muziseo.service.system.module.system.service.SysOssService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.stream.Collectors;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import cn.muziseo.service.system.module.system.manager.SysOssManager;
import cn.muziseo.service.system.module.system.repository.entity.SysOssConfigEntity;
import cn.muziseo.service.system.module.system.service.SysOssConfigService;

/**
 * OSS 资源服务 Controller
 *
 * @author 木子软件
 */
@Tag(name = "OSS资源管理")
@RestController
@Slf4j
@RequestMapping("/resource/oss")
public class SysOssController {

    @Resource
    private SysOssService sysOssService;

    @Resource
    private SysOssManager sysOssManager;

    @Resource
    private SysOssConfigService sysOssConfigService;

    @Operation(summary = "上传文件")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SaCheckPermission("system:oss:upload")
    @Log(title = "OSS资源", businessType = BusinessType.INSERT)
    public ResponseDTO<SysOssVO> upload(
            @RequestPart("file") MultipartFile file,
            @Parameter(description = "模块名 (用于分类存储)") @RequestParam(defaultValue = "default") String moduleName) {
        
        log.info("开始上传文件: originalName={}, module={}", file.getOriginalFilename(), moduleName);
        SysOssEntity oss = sysOssService.upload(file, moduleName);
        
        SysOssVO vo = SysOssVO.builder()
                .id(oss.getId())
                .fileName(oss.getFileName())
                .originalName(oss.getOriginalName())
                .fileSuffix(oss.getFileSuffix())
                .url(oss.getUrl())
                .size(oss.getSize())
                .contentType(oss.getContentType())
                .createTime(oss.getCreateTime())
                .build();
        
        return ResponseDTO.success(vo);
    }

    @Operation(summary = "删除文件")
    @DeleteMapping("/delete")
    @SaCheckPermission("system:oss:delete")
    @Log(title = "OSS资源", businessType = BusinessType.DELETE)
    public ResponseDTO<Void> delete(@RequestParam Long id) {
        log.info("删除文件资源: id={}", id);
        sysOssService.delete(id);
        return ResponseDTO.success();
    }

    @Operation(summary = "分页查询")
    @GetMapping("/page")
    @SaCheckPermission("system:oss:query")
    public ResponseDTO<Page<SysOssVO>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String originalName,
            @RequestParam(required = false) String fileSuffix,
            @RequestParam(required = false) String service) {

        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(SysOssEntity::getOriginalName).like(originalName)
                .and(SysOssEntity::getFileSuffix).eq(fileSuffix)
                .and(SysOssEntity::getService).eq(service)
                .orderBy(SysOssEntity::getId, false);

        Page<SysOssEntity> page = sysOssManager.page(new Page<>(pageNum, pageSize), queryWrapper);
        
        // 转换为 VO 分页
        Page<SysOssVO> voPage = new Page<>();
        voPage.setPageNumber(page.getPageNumber());
        voPage.setPageSize(page.getPageSize());
        voPage.setTotalRow(page.getTotalRow());
        
        voPage.setRecords(page.getRecords().stream().map(oss -> SysOssVO.builder()
                .id(oss.getId())
                .fileName(oss.getFileName())
                .originalName(oss.getOriginalName())
                .fileSuffix(oss.getFileSuffix())
                .url(oss.getUrl())
                .size(oss.getSize())
                .service(oss.getService())
                .contentType(oss.getContentType())
                .createTime(oss.getCreateTime())
                .build()).collect(Collectors.toList()));

        return ResponseDTO.success(voPage);
    }

    @Operation(summary = "下载/查看文件")
    @SaIgnore
    @GetMapping("/download/**")
    public void download(HttpServletRequest request, HttpServletResponse response) {
        // 从请求路径中提取原始 Key
        String path = request.getRequestURI();
        String key = path.substring(path.indexOf("/download/") + 10);
        log.info("访问文件资源: key={}", key);

        try {
            // 从当前生效的配置中获取本地存储根目录
            // 注意：这里简单实现，假设是本地策略。如果是其他策略，应该重定向到对应的 URL。
            // 但既然能进到这个方法且是 /download 路由，通常就是为了处理本地或需要中转的文件。
            SysOssEntity oss = sysOssManager.getOne(QueryWrapper.create().where(SysOssEntity::getFileName).eq(key));
            if (oss == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            // 获取配置
            SysOssConfigEntity config = sysOssConfigService.getByConfigKey(oss.getService());
            if (config == null) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }

            // 路径安全校验：防止路径遍历 (Path Traversal)
            File baseDir = new File(config.getEndpoint());
            File file = new File(baseDir, key);
            
            // 使用 Hutool 校验子路径是否在基准目录下
            if (!FileUtil.isSub(baseDir, file) || !file.exists()) {
                log.warn("非法文件访问尝试: key={}", key);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            // 默认设置响应头
            String contentType = Files.probeContentType(file.toPath());
            if (contentType == null) {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }
            response.setContentType(contentType);

            // 获取是否强制下载的参数
            String download = request.getParameter("download");
            
            // 如果是强制下载，或者不是图片，增加附件下载头
            if ("1".equals(download) || "true".equals(download) || !contentType.startsWith("image/")) {
                response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(oss.getOriginalName(), StandardCharsets.UTF_8));
            }

            // 写入流
            try (InputStream in = new FileInputStream(file);
                 OutputStream out = response.getOutputStream()) {
                byte[] buffer = new byte[8192];
                int len;
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
                out.flush();
            }
        } catch (Exception e) {
            log.error("文件查看失败", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}

package cn.muziseo.service.system.module.system.api;

import cn.muziseo.service.system.constants.ApiConstants;
import cn.muziseo.service.system.module.system.api.dto.FileRemoteDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件远程调用 RPC 接口
 *
 * @author 木子软件
 */
@FeignClient(name = ApiConstants.NAME, contextId = "fileApi")
public interface FileApi {

    String PREFIX = ApiConstants.PREFIX + "/file";

    /**
     * 上传文件
     *
     * @param file 待上传的文件
     * @return 远程文件数据对象
     */
    @PostMapping(value = PREFIX + "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    FileRemoteDTO uploadFile(@RequestPart("file") MultipartFile file);
}

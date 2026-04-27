package cn.muziseo.service.demo.module.log.controller;

import cn.muziseo.common.core.domain.dto.ResponseDTO;
import cn.muziseo.common.log.annotation.Log;
import cn.muziseo.common.log.enums.BusinessType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 操作日志功能演示
 */
@Tag(name = "日志演示")
@RestController
@RequestMapping("/demo/log")
public class LogDemoController {

    @Log(title = "日志演示-正常操作", businessType = BusinessType.INSERT)
    @Operation(summary = "正常操作演示")
    @PostMapping("/success")
    public ResponseDTO<String> success(@RequestBody Map<String, Object> params) {
        return ResponseDTO.success("操作成功，日志已生成（异步）");
    }

    @Log(title = "日志演示-异常操作", businessType = BusinessType.UPDATE)
    @Operation(summary = "异常操作演示")
    @PutMapping("/error")
    public ResponseDTO<Void> error() {
        throw new RuntimeException("演示：这是一次失败的操作，异常信息将被记录到日志中");
    }

    @Log(title = "日志演示-数据脱敏", businessType = BusinessType.OTHER)
    @Operation(summary = "数据脱敏演示")
    @PostMapping("/mask")
    public ResponseDTO<LogDemoDTO> mask(@RequestBody LogDemoDTO params) {
        // params 中标记了 @Sensitive 的字段，由于我们在 LogAspect 中配置了脱敏逻辑，
        // 日志中存储的参数将会自动进行掩码处理
        return ResponseDTO.success(params);
    }

    @Log(title = "日志演示-指定排除参数", businessType = BusinessType.DELETE, excludeParamNames = {"content"})
    @Operation(summary = "排除特定参数演示")
    @DeleteMapping("/exclude")
    public ResponseDTO<String> exclude(@RequestParam String id, @RequestParam String content) {
        // content 参数将被排除在日志记录之外
        return ResponseDTO.success("已排除 content 参数的记录");
    }

    @Log(title = "日志演示-保存响应数据", businessType = BusinessType.EXPORT, isSaveResponseData = true)
    @Operation(summary = "保存响应数据演示")
    @GetMapping("/response")
    public ResponseDTO<Map<String, String>> response() {
        Map<String, String> data = new HashMap<>();
        data.put("result", "这段文本会被完整保存在日志表的 json_result 字段中");
        return ResponseDTO.success(data);
    }
}

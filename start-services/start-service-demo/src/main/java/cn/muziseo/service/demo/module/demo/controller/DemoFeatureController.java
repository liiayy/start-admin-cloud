package cn.muziseo.service.demo.module.demo.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.muziseo.common.core.annotation.Idempotent;
import cn.muziseo.common.core.domain.dto.ResponseDTO;
import cn.muziseo.common.core.exception.BusinessException;
import cn.muziseo.common.websocket.utils.WebSocketUtils;
import cn.muziseo.service.demo.enums.DemoErrorCode;
import cn.muziseo.common.cache.config.ConfigUtils;
import cn.muziseo.common.cache.dict.DictUtils;
import cn.muziseo.common.core.constant.ConfigConstants;
import cn.muziseo.common.core.constant.DictConstants;
import cn.muziseo.service.demo.module.demo.controller.vo.DemoDictVO;
import cn.muziseo.service.demo.module.demo.controller.vo.DemoVO;
import cn.muziseo.service.demo.module.demo.service.DemoService;
import cn.muziseo.service.system.module.auth.api.UserApi;
import cn.muziseo.service.system.module.auth.api.dto.UserRemoteDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * 演示基座与高级特性 Controller
 *
 * @author Antigravity
 */
@Tag(name = "基座特性演示")
@RestController
@RequestMapping("/admin/demo/feature")
public class DemoFeatureController {

    @Resource
    private DemoService demoService;

    @Resource
    private UserApi userApi;

    @Operation(summary = "测试接口幂等提交 (防重)")
    @PostMapping("/idempotent")
    @Idempotent(time = 5, message = "您的请求正在处理中，5秒内请勿重复提交！")
    @SaCheckPermission("demo:cache:query")
    public ResponseDTO<String> idempotentTest(@RequestParam String data) {
        // 模拟处理业务
        return ResponseDTO.success("数据提交成功: " + data);
    }

    @Operation(summary = "测试统一业务异常拦截")
    @GetMapping("/exception")
    public ResponseDTO<Void> exceptionTest() {
        throw new BusinessException(DemoErrorCode.CUSTOM_DEMO_ERROR);
    }

    @Operation(summary = "测试系统制造的异常拦截 (自动记录日志)")
    @GetMapping("/sys-exception")
    public ResponseDTO<Void> sysExceptionTest() {
        throw new cn.muziseo.common.core.exception.ServiceException(
                cn.muziseo.common.core.exception.errorCode.SystemErrorCode.INTERNAL_ERROR,
                "演示系统致命异常上报！"
        );
    }

    @Operation(summary = "测试缓存获取 (Spring Cache)")
    @GetMapping("/cache")
    @SaCheckPermission("demo:cache:query")
    public ResponseDTO<DemoVO> cacheGet(@RequestParam Long id) {
        return ResponseDTO.success(demoService.getCachedProduct(id));
    }

    @Operation(summary = "测试缓存清空 (Spring Cache)")
    @DeleteMapping("/cache")
    @SaCheckPermission("demo:cache:query")
    public ResponseDTO<Void> cacheEvict(@RequestParam Long id) {
        demoService.evictCache(id);
        return ResponseDTO.success();
    }

    @Operation(summary = "测试手动分布式锁 (Redisson)")
    @PostMapping("/lock")
    @SaCheckPermission("demo:cache:query")
    public ResponseDTO<String> lockTest(@RequestParam String lockKey) {
        return ResponseDTO.success(demoService.executeWithLock(lockKey));
    }

    @Operation(summary = "测试 RPC 远程调用 (调用 system-service)")
    @GetMapping("/rpc/user")
    @SaCheckPermission("demo:cache:query")
    public ResponseDTO<UserRemoteDTO> rpcUserTest(@RequestParam Long id) {
        return ResponseDTO.success(userApi.getUserById(id));
    }

    @Operation(summary = "测试 Seata 分布式事务")
    @PostMapping("/seata")
    @SaCheckPermission("demo:cache:query")
    public ResponseDTO<Void> seataTest(
            @RequestParam Long userId,
            @RequestParam String nickname,
            @RequestParam String demoName,
            @RequestParam boolean throwEx) {
        demoService.testSeata(userId, nickname, demoName, throwEx);
        return ResponseDTO.success();
    }

    @Operation(summary = "测试 WebSocket 消息广播")
    @PostMapping("/push/broadcast")
    @SaCheckPermission("demo:websocket:query")
    public ResponseDTO<Void> pushBroadcast(@RequestParam String title, @RequestParam String message) {
        WebSocketUtils.pushNotificationAll(title, message);
        return ResponseDTO.success();
    }

    @Operation(summary = "测试 WebSocket 个人定向推送")
    @PostMapping("/push/user")
    @SaCheckPermission("demo:websocket:query")
    public ResponseDTO<Void> pushToUser(@RequestParam Long userId, @RequestParam String title, @RequestParam String message) {
        WebSocketUtils.pushNotification(userId, title, message);
        return ResponseDTO.success();
    }

    @Operation(summary = "演示在demo服务中使用字典与系统参数")
    @GetMapping("/dict-system-demo")
    public ResponseDTO<DemoDictVO> dictSystemDemo(
            @RequestParam(required = false, defaultValue = "1") Integer sex,
            @RequestParam(required = false, defaultValue = "0") String status) {
        DemoDictVO vo = new DemoDictVO();
        vo.setSex(sex);
        vo.setStatus(status);

        // 演示一：通过 ConfigUtils 获取系统参数配置
        boolean captchaEnabled = ConfigUtils.getBoolean(ConfigConstants.CAPTCHA_ENABLED, true);
        String defaultPassword = ConfigUtils.getString(ConfigConstants.USER_DEFAULT_PASSWORD, "123456");
        vo.setCaptchaEnabled(captchaEnabled);
        vo.setDefaultPassword(defaultPassword);

        // 演示二：手动使用 DictUtils 翻译字典
        String manualLabel = DictUtils.getDictLabel(DictConstants.SYS_USER_SEX, sex);
        vo.setManualSexLabel(manualLabel);

        // 演示三：通过在 DemoDictVO 的属性上使用 @Dict 注解实现自动拦截翻译。
        // （返回值是 ResponseDTO 且 VO 中含有 @Dict 标注的字段，DictTranslationAdvice 会在返回时拦截并自动填充翻译字段）
        return ResponseDTO.success(vo);
    }
}

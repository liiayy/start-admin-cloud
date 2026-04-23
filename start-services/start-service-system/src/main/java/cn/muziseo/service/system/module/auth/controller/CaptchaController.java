package cn.muziseo.service.system.module.auth.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.muziseo.common.cache.utils.RedisUtils;
import cn.muziseo.common.core.constant.ConfigConstants;
import cn.muziseo.common.core.domain.dto.ResponseDTO;
import cn.muziseo.common.cache.config.ConfigUtils;
import cn.muziseo.service.system.module.auth.controller.vo.CaptchaVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 验证码 Controller
 *
 * @author 木子软件
 */
@Tag(name = "验证码")
@RestController
@RequestMapping("/admin/system/auth")
public class CaptchaController {

    private static final String CAPTCHA_CODE_KEY = "captcha_codes:";
    private static final long CAPTCHA_EXPIRATION_MINUTES = 2;

    @Operation(summary = "获取验证码")
    @GetMapping("/captcha")
    public ResponseDTO<CaptchaVO> getCaptcha() {
        boolean captchaEnabled = ConfigUtils.getBoolean(ConfigConstants.CAPTCHA_ENABLED, true);
        if (!captchaEnabled) {
            return ResponseDTO.success(CaptchaVO.builder().enabled(false).build());
        }

        // 1. 生成验证码图片
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(120, 40, 4, 20);
        String code = lineCaptcha.getCode();
        String imageBase64 = lineCaptcha.getImageBase64Data();

        // 2. 生成 UUID 并存储至 Redis
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String redisKey = CAPTCHA_CODE_KEY + uuid;
        RedisUtils.setCacheObject(redisKey, code, Duration.ofMinutes(CAPTCHA_EXPIRATION_MINUTES));

        // 3. 构建返回结果
        CaptchaVO captchaVO = CaptchaVO.builder()
                .uuid(uuid)
                .img(imageBase64)
                .enabled(true)
                .build();
        return ResponseDTO.success(captchaVO);
    }
}

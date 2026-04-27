package cn.muziseo.common.sentinel.core.config;


import cn.muziseo.common.core.constant.HttpStatus;
import cn.muziseo.common.core.domain.dto.ResponseDTO;
import cn.muziseo.common.core.exception.errorCode.CommonErrorCode;
import cn.muziseo.common.core.utils.json.JsonUtils;
import com.alibaba.csp.sentinel.adapter.spring.webmvc_v6x.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

import java.io.IOException;

/**
 * Sentinel 配置类，用于统一微服务的限流返回格式
 */
@AutoConfiguration
@ConditionalOnClass(BlockExceptionHandler.class) // 只有引入了 Sentinel 适配器时才生效
@Slf4j
public class StartSentinelAutoConfiguration {
    @Bean
    public BlockExceptionHandler blockExceptionHandler() {
        return (HttpServletRequest request, HttpServletResponse response, String var3, BlockException e) -> {
            log.warn("[Sentinel限流] 资源名称: {}, 异常类型: {}", e.getRule().getResource(), e.getClass().getSimpleName());

            // 2. 构造统一返回对象
            ResponseDTO<?> result = e instanceof DegradeException
                    ? ResponseDTO.fail(CommonErrorCode.OPERATION_TOO_FREQUENT, "服务已降级，请稍候重试")
                    : ResponseDTO.fail(CommonErrorCode.OPERATION_TOO_FREQUENT);

            // 3. 设置 Response 状态
            response.setStatus(200); // 即使限流也返回 200 HTTP 状态码，由业务 code 区分
            response.setContentType("application/json;charset=utf-8");

            // 4. 使用之前配置好的 JsonUtils 进行序列化，确保 Long 精度和时间格式一致
            try {
                response.getWriter().print(JsonUtils.toJsonString(result));
            } catch (IOException ex) {
                log.error("[Sentinel限流] 序列化响应失败", ex);
            }
        };
    }
}

package cn.muziseo.gateway.exception;

import cn.hutool.core.util.StrUtil;
import cn.muziseo.common.core.constant.HttpStatus;
import cn.muziseo.common.core.domain.dto.ResponseDTO;
import cn.muziseo.common.core.exception.errorCode.CommonErrorCode;
import cn.muziseo.common.core.exception.errorCode.SystemErrorCode;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Order(-1)
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, @NonNull Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        if (response.isCommitted()) {
            return Mono.error(ex);
        }

        ResponseDTO<?> result;

        // 1. 处理 Sentinel 限流/降级异常
        if (BlockException.isBlockException(ex)) {
            result = handleBlockException(ex);
        }
        // 2. 处理 Spring 标准响应异常 (如 404, 405)
        else if (ex instanceof ResponseStatusException rse) {
            result = ResponseDTO.fail(rse.getStatusCode().value(),
                    StrUtil.blankToDefault(rse.getReason(), "网关请求失败"));
        }
        // 3. 兜底处理系统异常
        else {
            result = handleInternalException(exchange, ex);
        }

        return writeJSON(exchange, result);
    }

    /**
     * 处理 Sentinel 异常，翻译为业务文案
     */
    private ResponseDTO<?> handleBlockException(Throwable ex) {
        if (ex instanceof FlowException) {
            return ResponseDTO.fail(CommonErrorCode.OPERATION_TOO_FREQUENT);
        } else if (ex instanceof DegradeException) {
            return ResponseDTO.fail(CommonErrorCode.OPERATION_TOO_FREQUENT, "服务已降级，请稍候重试");
        } else if (ex instanceof ParamFlowException) {
            return ResponseDTO.fail(CommonErrorCode.OPERATION_TOO_FREQUENT, "热点参数限流");
        }
        return ResponseDTO.fail(CommonErrorCode.OPERATION_TOO_FREQUENT);
    }

    private ResponseDTO<?> handleInternalException(ServerWebExchange exchange, Throwable ex) {
        ServerHttpRequest request = exchange.getRequest();
        log.error("[网关系统异常] 路径: {}, 信息: ", request.getURI().getPath(), ex);
 
        // 识别服务不可用的常见情况
        String msg = ex.getMessage();
        if (msg != null && msg.contains("Unable to find instance for")) {
            return ResponseDTO.fail(SystemErrorCode.INTERNAL_ERROR, "服务暂时不可用，请稍后再试");
        }
 
        return ResponseDTO.fail(SystemErrorCode.INTERNAL_ERROR);
    }

    private Mono<Void> writeJSON(ServerWebExchange exchange, ResponseDTO<?> responseDTO) {
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        response.setStatusCode(org.springframework.http.HttpStatus.OK);

        return response.writeWith(Mono.fromSupplier(() -> {
            DataBufferFactory bufferFactory = response.bufferFactory();
            try {
                return bufferFactory.wrap(objectMapper.writeValueAsBytes(responseDTO));
            } catch (JsonProcessingException e) {
                return bufferFactory.wrap("{\"code\":500,\"msg\":\"Internal Server Error\"}".getBytes());
            }
        }));
    }
}
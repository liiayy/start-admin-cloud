package cn.muziseo.gateway.exception;

import cn.muziseo.common.core.constant.HttpStatus;
import cn.muziseo.common.core.domain.dto.ResponseDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Gateway 的全局异常处理器，将 Exception 翻译成 ResponseDTO + 对应的异常编号
 *
 * @author 木子软件
 * @Date 2026-01-14
 */
@Component
@Order(-1) // 保证优先级高于默认的 Spring Cloud Gateway 的 ErrorWebExceptionHandler 实现
@Slf4j
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, @NonNull Throwable ex) {
        //获取响应对象
        ServerHttpResponse response = exchange.getResponse();
        //response是否结束  用于多个异常处理时候
        if (response.isCommitted()) {
            return Mono.error(ex);
        }

        // 转换成 ResponseDTO
        ResponseDTO<?> result;
        if (ex instanceof ResponseStatusException) {
            result = responseStatusExceptionHandler(exchange, (ResponseStatusException) ex);
        } else {
            result = defaultExceptionHandler(exchange, ex);
        }

        // 返回给前端
        return writeJSON(exchange, result);
    }

    /**
     * 处理 Spring Cloud Gateway 默认抛出的 ResponseStatusException 异常
     *
     * @param exchange ServerWebExchange
     * @param ex       ResponseStatusException
     * @return ResponseDTO
     */
    private ResponseDTO<?> responseStatusExceptionHandler(ServerWebExchange exchange,
                                                         ResponseStatusException ex) {
        ServerHttpRequest request = exchange.getRequest();
        log.error("[responseStatusExceptionHandler][uri({}/{}) 发生异常]", request.getURI(), request.getMethod(), ex);
        // 转换为用户友好的错误信息
        String message = ex.getReason();
        if (message == null || message.isEmpty()) {
            message = "请求处理失败";
        }
        return ResponseDTO.fail(ex.getStatusCode().value(), message);
    }

    /**
     * 处理系统异常，兜底处理所有的一切
     *
     * @param exchange ServerWebExchange
     * @param ex       Throwable
     * @return ResponseDTO
     */
    private ResponseDTO<?> defaultExceptionHandler(ServerWebExchange exchange,
                                                  Throwable ex) {
        ServerHttpRequest request = exchange.getRequest();
        log.error("[defaultExceptionHandler][uri({}/{}) 发生异常]", request.getURI(), request.getMethod(), ex);
        // 返回 ERROR ResponseDTO
        return ResponseDTO.fail(HttpStatus.INTERNAL_SERVER_ERROR, "系统内部错误");
    }


    /**
     * 将对象写入到响应中
     *
     * @param exchange 交换对象
     * @param response 响应对象
     * @return Mono<Void>
     */
    public static Mono<Void> writeJSON(ServerWebExchange exchange, ResponseDTO<?> response) {
        // 设置响应头
        exchange.getResponse().getHeaders().set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        exchange.getResponse().getHeaders().set(HttpHeaders.CACHE_CONTROL, "no-store, no-cache, must-revalidate, max-age=0");

        // 序列化响应对象
        byte[] bytes;
        try {
            bytes = OBJECT_MAPPER.writeValueAsBytes(response);
        } catch (JsonProcessingException e) {
            log.error("[writeJSON] 序列化响应对象失败", e);
            // 序列化失败时，返回系统错误
            return writeJSON(exchange, ResponseDTO.fail("系统内部错误"));
        }

        // 写入响应
        DataBufferFactory bufferFactory = exchange.getResponse().bufferFactory();
        DataBuffer buffer = bufferFactory.wrap(bytes);
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }


}

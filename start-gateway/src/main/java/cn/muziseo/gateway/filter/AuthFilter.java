package cn.muziseo.gateway.filter;

import cn.dev33.satoken.stp.StpUtil;
import cn.muziseo.common.core.domain.dto.ResponseDTO;
import cn.muziseo.common.core.exception.errorCode.CommonErrorCode;
import cn.muziseo.common.core.exception.errorCode.IErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cn.muziseo.gateway.config.AuthWhiteProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Sa-Token 认证过滤器
 * <p>
 * 网关层校验 JWT Token 有效性，未登录请求直接拒绝。
 * 白名单路径（登录等）无需 Token 即可通过。
 * 校验通过后将 userId 通过 Header 透传给下游服务。
 *
 * @author 木子软件
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class AuthFilter implements GlobalFilter, Ordered {

    private final ObjectMapper objectMapper;
    private final AuthWhiteProperties authWhiteProperties;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 【关键修复】手动绑定 Reactor 上下文，确保 StpUtil 能读取到环境变量和请求信息
        cn.dev33.satoken.reactor.context.SaReactorSyncHolder.setContext(exchange);

        String path = exchange.getRequest().getPath().value();

        // 1. 白名单放行
        if (isWhitePath(path)) {
            return chain.filter(exchange);
        }

        // 2. 校验 Sa-Token JWT
        Object loginId;
        try {
            loginId = StpUtil.getLoginIdDefaultNull();
        } catch (Exception e) {
            log.error("Token 解析异常: path={}", path, e);
            return writeError(exchange, CommonErrorCode.UNAUTHORIZED, "无效的认证凭证: " + e.getMessage());
        }

        if (loginId == null) {
            return writeError(exchange, CommonErrorCode.UNAUTHORIZED);
        }

        // 3. 将 userId 透传给下游服务
        ServerHttpRequest request = exchange.getRequest().mutate()
                .header("X-User-Id", loginId.toString())
                .build();

        return chain.filter(exchange.mutate().request(request).build());
    }

    /**
     * 判断是否为白名单路径
     */
    private boolean isWhitePath(String path) {
        // 1. 前缀匹配
        for (String prefix : authWhiteProperties.getWhitePrefixes()) {
            if (path.startsWith(prefix)) {
                return true;
            }
        }

        // 2. 精确匹配
        for (String white : authWhiteProperties.getWhitePaths()) {
            if (path.equals(white)) {
                return true;
            }
        }
 
        // 3. 后缀匹配
        for (String suffix : authWhiteProperties.getWhiteSuffixes()) {
            if (path.endsWith(suffix)) {
                return true;
            }
        }
 
        return false;
    }

    /**
     * 返回错误响应
     */
    private Mono<Void> writeError(ServerWebExchange exchange, IErrorCode errorCode) {
        return writeError(exchange, errorCode, errorCode.getMessage());
    }

    /**
     * 返回错误响应
     */
    private Mono<Void> writeError(ServerWebExchange exchange, IErrorCode errorCode, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(org.springframework.http.HttpStatus.OK);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        ResponseDTO<?> result = ResponseDTO.fail(errorCode, message);

        return response.writeWith(Mono.fromSupplier(() -> {
            DataBufferFactory bufferFactory = response.bufferFactory();
            try {
                return bufferFactory.wrap(objectMapper.writeValueAsBytes(result));
            } catch (JsonProcessingException e) {
                return bufferFactory.wrap("{\"code\":401,\"msg\":\"Unauthorized\"}".getBytes());
            }
        }));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}

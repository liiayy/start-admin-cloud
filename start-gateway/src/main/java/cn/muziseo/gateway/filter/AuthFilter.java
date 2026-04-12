package cn.muziseo.gateway.filter;

import cn.dev33.satoken.stp.StpUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cn.muziseo.common.core.domain.dto.ResponseDTO;
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

import java.util.List;
import java.util.Set;

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

    /**
     * 白名单路径前缀（不需要认证）
     * <p>
     * 网关看到的路径是带路由前缀的，如 /api/system/auth/login
     */
    private static final Set<String> WHITE_PREFIXES = Set.of(
            "/auth/login",
            "/auth/captcha"
    );

    /**
     * 白名单路径前缀（文档、监控等）
     */
    private static final Set<String> WHITE_PATH_PREFIXES = Set.of(
            "/swagger-ui",
            "/v3/api-docs",
            "/actuator"
    );

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
            return writeError(exchange, HttpStatus.UNAUTHORIZED.value(), "无效的认证凭证: " + e.getMessage());
        }

        if (loginId == null) {
            return writeError(exchange, HttpStatus.UNAUTHORIZED.value(), "未登录或登录已过期");
        }

        // 3. 将 userId 透传给下游服务
        ServerHttpRequest request = exchange.getRequest().mutate()
                .header("X-User-Id", loginId.toString())
                .build();

        return chain.filter(exchange.mutate().request(request).build());
    }

    /**
     * 判断是否为白名单路径
     * <p>
     * 路由路径格式：/api/{service}/{actual-path}
     * 需要匹配去掉路由前缀后的实际路径
     */
    private boolean isWhitePath(String path) {
        // 精确匹配文档和监控路径
        for (String prefix : WHITE_PATH_PREFIXES) {
            if (path.startsWith(prefix)) {
                return true;
            }
        }

        // 匹配业务白名单：/api/system/auth/login → 提取 /auth/login
        String actualPath = extractActualPath(path);
        if (actualPath != null) {
            for (String white : WHITE_PREFIXES) {
                if (actualPath.equals(white) || actualPath.startsWith(white + "/")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 从网关路径中提取实际业务路径
     * <p>
     * /api/system/auth/login → /auth/login
     * /api/demo/auth/login   → /auth/login
     */
    private String extractActualPath(String path) {
        // 去掉 /api 前缀
        if (!path.startsWith("/api/")) {
            return path;
        }
        String rest = path.substring(5); // "system/auth/login"
        int slashIndex = rest.indexOf('/');
        if (slashIndex < 0) {
            return null;
        }
        return rest.substring(slashIndex); // "/auth/login"
    }

    /**
     * 返回错误响应
     */
    private Mono<Void> writeError(ServerWebExchange exchange, int code, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        ResponseDTO<?> result = ResponseDTO.fail(code, message);

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

package cn.muziseo.gateway.filter;
 
import cn.hutool.core.util.IdUtil;
import cn.muziseo.common.core.constant.TraceConstants;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
 
/**
 * 链路追踪过滤器
 * <p>
 * 在网关入口生成 TraceId，并透传给下游服务。
 *
 * @author 木子软件
 */
@Component
public class TraceFilter implements GlobalFilter, Ordered {
 
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1. 获取或生成 TraceId
        String traceId = exchange.getRequest().getHeaders().getFirst(TraceConstants.TRACE_ID_HEADER);
        if (traceId == null || traceId.isEmpty()) {
            traceId = IdUtil.fastSimpleUUID();
        }
 
        // 2. 将 TraceId 放入网关自身的 MDC（供网关日志打印）
        MDC.put(TraceConstants.TRACE_ID, traceId);
 
        // 3. 构建新的请求并透传 Header
        ServerHttpRequest request = exchange.getRequest().mutate()
                .header(TraceConstants.TRACE_ID_HEADER, traceId)
                .build();
 
        // 4. 将 TraceId 放入响应头回显（在提交响应前最后一刻设置，确保覆盖下游微服务的重复 Header）
        String finalTraceId = traceId;
        exchange.getResponse().beforeCommit(() -> {
            exchange.getResponse().getHeaders().set(TraceConstants.TRACE_ID_HEADER, finalTraceId);
            return Mono.empty();
        });
 
        return chain.filter(exchange.mutate().request(request).build())
                .doFinally(signalType -> MDC.remove(TraceConstants.TRACE_ID));
    }
 
    @Override
    public int getOrder() {
        // 优先级最高，确保在 AuthFilter 之前执行
        return Ordered.HIGHEST_PRECEDENCE;
    }
}

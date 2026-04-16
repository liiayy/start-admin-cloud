package cn.muziseo.gateway.filter;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.rewrite.CachedBodyOutputMessage;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * 请求验证过滤器
 *
 * @author 木子SEO
 * @Date 2026-01-15
 * @Url <a href="https://spring-doc.muziseo.cn/spring-cloud/gateway/developer-guide.html#_3-%E7%BC%96%E5%86%99%E8%87%AA%E5%AE%9A%E4%B9%89%E5%85%A8%E5%B1%80%E8%BF%87%E6%BB%A4%E5%99%A8-global-filters">https://spring-doc.muziseo.cn/spring-cloud/gateway/#_request_validation_filter</a>
 */
@Component
@Slf4j
public class RequestValidationFilter implements GlobalFilter, Ordered {

    @Resource
    private ServerCodecConfigurer serverCodecConfigurer;

    @Override
    public Mono<Void> filter(final ServerWebExchange exchange, final GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        MediaType contentType = request.getHeaders().getContentType();
        long contentLength = request.getHeaders().getContentLength();

        // 1. 过滤不适合读取 Body 的请求（如文件上传、二进制流、过大的请求）
        if (isBinaryOrTooLarge(contentType, contentLength)) {
            return chain.filter(exchange);
        }

        return readBody(exchange, chain);
    }

    /**
     * 判断是否为二进制内容或请求体过大（防止 OOM）
     */
    private boolean isBinaryOrTooLarge(MediaType contentType, long contentLength) {
        // 请求体超过 2MB 则不处理
        if (contentLength > 2 * 1024 * 1024) {
            return true;
        }
        // 如果没有内容类型，或者是文件上传/二进制流，跳过读取
        if (contentType == null) {
            return false;
        }
        return contentType.includes(MediaType.MULTIPART_FORM_DATA)
                || contentType.includes(MediaType.APPLICATION_OCTET_STREAM)
                || contentType.includes(MediaType.IMAGE_GIF)
                || contentType.includes(MediaType.IMAGE_JPEG)
                || contentType.includes(MediaType.IMAGE_PNG);
    }

    /**
     * 读取请求体
     *
     * @param exchange ServerWebExchange
     * @param chain    GatewayFilterChain
     * @return Mono<Void>
     */
    private Mono<Void> readBody(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.debug("开始读取请求体: path={}", exchange.getRequest().getPath());

        ServerRequest serverRequest = ServerRequest.create(exchange, serverCodecConfigurer.getReaders());

        Map<String, String> headMap = new HashMap<>();
        Mono<String> modifiedBody = serverRequest.bodyToMono(String.class).flatMap(originalBody -> {
            // 处理请求体和请求头逻辑的方法
            String body = handle(originalBody, exchange, headMap);
            return Mono.just(body);
        });

        BodyInserter bodyInserter = BodyInserters.fromPublisher(modifiedBody, String.class);
        HttpHeaders headers = new HttpHeaders();
        headers.putAll(exchange.getRequest().getHeaders());
        headers.remove(HttpHeaders.CONTENT_LENGTH);

        CachedBodyOutputMessage outputMessage = new CachedBodyOutputMessage(exchange, headers);
        return bodyInserter
                .insert(outputMessage, new BodyInserterContext())
                .then(Mono.defer(() -> chain.filter(
                        exchange.mutate().request(decorateHead(exchange, headers, outputMessage, headMap)).build()
                )))
                .onErrorResume((Function<Throwable, Mono<Void>>) throwable -> Mono.error(throwable));
    }

    /**
     * 处理请求体和请求头
     *
     * @param originalBody 原始请求体
     * @param exchange     ServerWebExchange
     * @param headMap      请求头映射
     * @return 处理后的请求体
     */
    private String handle(String originalBody, ServerWebExchange exchange, Map<String, String> headMap) {
        String path = exchange.getRequest().getPath().value();
        // 生产环境严禁在 INFO 级别打印全量 Body，改为 DEBUG
        log.debug("请求校验 - 路径: {}, 请求体: {}", path, originalBody);
        
        // 此处可扩展脱敏逻辑或特定参数校验
        return originalBody;
    }

    /**
     * 装饰请求头，将网关层的重要参数传递给后续的微服务
     *
     * @param exchange      ServerWebExchange
     * @param headers       HttpHeaders
     * @param outputMessage CachedBodyOutputMessage
     * @param headMap       请求头映射
     * @return ServerHttpRequestDecorator
     */
    private ServerHttpRequestDecorator decorateHead(ServerWebExchange exchange, HttpHeaders headers,
                                                    CachedBodyOutputMessage outputMessage, Map<String, String> headMap) {
        return new ServerHttpRequestDecorator(exchange.getRequest()) {

            @Override
            public HttpHeaders getHeaders() {
                log.info("current thread getHeaders: {}", Thread.currentThread().getName());
                long contentLength = headers.getContentLength();
                HttpHeaders newHeaders = new HttpHeaders();
                newHeaders.putAll(headers);

                if (headMap != null) {
                    newHeaders.setAll(headMap);
                }

                if (contentLength > 0) {
                    newHeaders.setContentLength(contentLength);
                } else {
                    newHeaders.set(HttpHeaders.TRANSFER_ENCODING, "chunked");
                }

                return newHeaders;
            }

            @Override
            public Flux<DataBuffer> getBody() {
                return outputMessage.getBody();
            }
        };
    }

    @Override
    public int getOrder() {
        return -2;
    }

}

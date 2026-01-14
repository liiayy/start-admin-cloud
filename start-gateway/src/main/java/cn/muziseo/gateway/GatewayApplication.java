package cn.muziseo.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * API网关启动类
 *
 * AI提示：
 * 1. 网关服务不需要数据库，所以不需要@MapperScan
 * 2. 网关主要职责：路由转发、认证鉴权、限流熔断、日志监控
 */
@SpringBootApplication(scanBasePackages = {"cn.muziseo.gateway"}) //scanBasePackages指定扫描的包，默认是当前类所在包及其子包
@EnableDiscoveryClient //开启服务发现功能，注册到服务注册中心
public class GatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
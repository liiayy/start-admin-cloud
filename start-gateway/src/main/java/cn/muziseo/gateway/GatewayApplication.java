package cn.muziseo.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * API网关启动类
 * 
 * @author 木子软件
 * @Date 2026-01-14
 * @Url <a href="https://spring-doc.muziseo.cn/spring-cloud/gateway/">https://spring-doc.muziseo.cn/spring-cloud/gateway/</a>
 */
@SpringBootApplication(scanBasePackages = {"cn.muziseo.gateway"})
@EnableDiscoveryClient
public class GatewayApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
    
}
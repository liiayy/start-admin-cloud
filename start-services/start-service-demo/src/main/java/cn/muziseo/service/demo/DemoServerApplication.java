package cn.muziseo.service.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 演示微服务启动类
 *
 * @author 木子软件
 */
@SpringBootApplication(scanBasePackages = {"cn.muziseo.service.demo"})
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "cn.muziseo")
@EnableAsync
@org.mybatis.spring.annotation.MapperScan("cn.muziseo.service.demo.module.**.repository.mapper")
public class DemoServerApplication {
    static void main(String[] args) {
        SpringApplication.run(DemoServerApplication.class, args);
    }
}

package cn.muziseo.service.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * system 模块下，我们放通用业务，支撑上层的核心业务。
 * 例如说：用户、部门、权限、数据字典等等
 *
 * @author 木子软件: 李彦军
 * @Date 2026/1/7
 * @Wechat liiayy
 * @Email 773582348@qq.com
 * @Copyright <a href="https://code.muziseo.cn">木子软件</a>
 */
@SpringBootApplication(scanBasePackages = {"cn.muziseo.service.system"})
@EnableDiscoveryClient
@EnableFeignClients
public class SystemServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(SystemServerApplication.class, args);
    }
}


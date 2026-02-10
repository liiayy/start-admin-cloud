package cn.muziseo.commom.enb.core.config;

import cn.muziseo.common.core.utils.string.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.util.List;

/**
 * YAML 配置文件处理器
 * <p>
 * 在 Spring Boot 启动早期加载所有匹配 classpath*:start-*.yaml 模式的配置文件
 * </p>
 *
 * @author 木子软件:李彦军
 * @Date 2022-05-30 21:22:12
 * @Wechat liiayy
 * @Email 773582348@qq.com
 * @Copyright <a href="https://code.muziseo.cn">木子软件</a>
 */
@Configuration
@Slf4j
@Order(value = 0)
public class YamlProcessor implements EnvironmentPostProcessor {

    /**
     * YAML 属性源加载器
     */
    private final YamlPropertySourceLoader loader = new YamlPropertySourceLoader();

    /**
     * 在 Spring Boot 启动早期处理环境配置
     * <p>
     * 1. 设置日志路径系统属性
     * 2. 加载所有 start-*.yaml 配置文件
     * </p>
     *
     * @param environment Spring 可配置环境
     * @param application Spring 应用实例
     */
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {

        String filePath = environment.getProperty("project.log-path");
        if (StringUtils.isNotEmpty(filePath)) {
            System.setProperty("project.log-path", filePath);
        }

        MutablePropertySources propertySources = environment.getPropertySources();
        this.loadProperty(propertySources);
    }

    /**
     * 加载 classpath 路径下所有匹配 start-*.yaml 模式的配置文件
     *
     * @param propertySources 可变的属性源集合
     */
    private void loadProperty(MutablePropertySources propertySources) {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            Resource[] resources = resolver.getResources("classpath*:start-*.yaml");
            if (resources.length < 1) {
                return;
            }
            for (Resource resource : resources) {
                log.info("初始化系统配置：{}", resource.getFilename());
                List<PropertySource<?>> load = loader.load(resource.getFilename(), resource);
                load.forEach(propertySources::addLast);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

package cn.muziseo.common.core.factory;

import cn.muziseo.common.core.utils.StringUtils;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.DefaultPropertySourceFactory;
import org.springframework.core.io.support.EncodedResource;

import java.io.IOException;
import java.util.Objects;

/**
 * 自定义 YML 属性源工厂类，用于支持 YML/YAML 格式的配置文件加载
 * <p>
 * 继承自 Spring 的 {@link DefaultPropertySourceFactory}，增加对 YML/YAML 格式配置文件的支持
 *
 * @Author 木子
 * @Date 2025/11/7周五 08:12:25
 * @Wechat liiayy
 * @Email 773582348@qq.com
 * @Copyright <a href="https://muziseo.cn">木子</a>
 */
public class YmlPropertySourceFactory extends DefaultPropertySourceFactory {

    /**
     * 支持的 YML/YAML 文件后缀
     */
    private static final String[] SUPPORTED_YML_SUFFIXES = {".yml", ".yaml"};

    /**
     * 创建属性源
     *
     * @param name     属性源名称（可选）
     * @param resource 编码的资源
     * @return 属性源对象
     * @throws IOException 如果资源访问失败
     */
    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {
        String sourceName = getSourceName(resource);
        if (isYmlFile(sourceName)) {
            return createYmlPropertySource(sourceName, resource);
        }
        return super.createPropertySource(name, resource);
    }

    /**
     * 判断是否为 YML/YAML 文件
     *
     * @param fileName 文件名
     * @return true 如果是 YML/YAML 文件，否则返回 false
     */
    private boolean isYmlFile(String fileName) {
        return StringUtils.isNotBlank(fileName)
                && StringUtils.endsWithAny(fileName, SUPPORTED_YML_SUFFIXES);
    }

    /**
     * 创建 YML 属性源
     *
     * @param sourceName 资源名称
     * @param resource   编码的资源
     * @return 属性源对象
     */
    private PropertySource<?> createYmlPropertySource(String sourceName, EncodedResource resource) {
        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        factory.setResources(resource.getResource());
        factory.afterPropertiesSet();
        return new PropertiesPropertySource(sourceName, Objects.requireNonNull(factory.getObject()));
    }

    /**
     * 获取资源文件名
     *
     * @param resource 编码的资源
     * @return 文件名
     */
    private String getSourceName(EncodedResource resource) {
        return resource.getResource().getFilename();
    }
}

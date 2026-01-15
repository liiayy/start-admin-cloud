package cn.muziseo.common.db.config;

import cn.muziseo.common.db.filter.DruidAdRemoveFilter;
import com.alibaba.druid.spring.boot3.autoconfigure.DruidDataSourceBuilder;
import com.alibaba.druid.spring.boot3.autoconfigure.properties.DruidStatProperties;
import com.alibaba.druid.util.Utils;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.servlet.*;

/**
 * Druid 配置
 * 
 * @Author 木子软件
 * @Date 2026-01-15
 */
@AutoConfiguration
@EnableTransactionManagement
@EnableConfigurationProperties(DruidStatProperties.class)
public class DruidConfig {
    
    /**
     * 移除 Druid 广告
     * 
     * @param properties DruidStatProperties
     * @return FilterRegistrationBean<DruidAdRemoveFilter>
     */
    @Bean
    @ConditionalOnProperty(name = "spring.datasource.druid.stat-view-servlet.enabled", havingValue = "true")
    public FilterRegistrationBean<DruidAdRemoveFilter> removeDruidAdFilterRegistrationBean(DruidStatProperties properties) {
        // 获取监控页面的参数
        DruidStatProperties.StatViewServlet config = properties.getStatViewServlet();
        
        // 提取common.js的配置路径
        String pattern = config.getUrlPattern() != null ? config.getUrlPattern() : "/druid/*";
        String commonJsPattern = pattern.replaceAll("\\*", "js/common.js");
        
        DruidAdRemoveFilter filter = new DruidAdRemoveFilter();
        
        FilterRegistrationBean<DruidAdRemoveFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(filter);
        registrationBean.addUrlPatterns(commonJsPattern);
        return registrationBean;
    }
    
}

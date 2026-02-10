package cn.muziseo.common.db.config;

import cn.muziseo.common.db.filter.DruidAdRemoveFilter;
import com.alibaba.druid.spring.boot3.autoconfigure.properties.DruidStatProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Druid 数据源配置
 * <p>
 * 配置阿里巴巴 Druid 数据库连接池，包括监控统计、SQL 防火墙等功能
 * 移除 Druid 监控页面的底部广告信息
 * </p>
 *
 * @author 木子软件
 * @Date 2026-01-15
 * @Wechat liiayy
 * @Email 773582348@qq.com
 * @Copyright <a href="https://code.muziseo.cn">木子软件</a>
 */
@AutoConfiguration
@EnableTransactionManagement
@EnableConfigurationProperties(DruidStatProperties.class)
public class DruidConfig {

    /**
     * 注册 Druid 广告移除过滤器
     * <p>
     * 通过过滤器拦截 Druid 监控页面的 common.js 文件，移除底部的广告信息
     * 仅当 Druid 监控页面启用时才注册此过滤器
     * </p>
     *
     * @param properties Druid 统计属性配置
     * @return 过滤器注册 Bean
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

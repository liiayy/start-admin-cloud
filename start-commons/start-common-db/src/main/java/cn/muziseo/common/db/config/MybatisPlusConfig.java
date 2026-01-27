package cn.muziseo.common.db.config;

import cn.muziseo.common.core.factory.YmlPropertySourceFactory;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * MyBatis Plus 配置
 *
 * @Author 木子软件
 * @Date 2026-01-15
 */
@EnableTransactionManagement
@AutoConfiguration(before = MybatisPlusAutoConfiguration.class)
@MapperScan(value = "${start.info.entity-package}", annotationClass = Mapper.class)
@PropertySource(value = "classpath:common-db.yml", factory = YmlPropertySourceFactory.class)
public class MybatisPlusConfig {

    /**
     * 分页插件
     *
     * @return MybatisPlusInterceptor
     */
    @Bean
    public MybatisPlusInterceptor paginationInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

}

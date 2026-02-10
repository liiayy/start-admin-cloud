package cn.muziseo.common.db.config;

import cn.muziseo.common.core.factory.YmlPropertySourceFactory;
import cn.muziseo.common.db.decipher.Decipher;
import cn.muziseo.common.db.listener.EntityInsertListener;
import cn.muziseo.common.db.listener.EntityUpdateListener;
import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.audit.AuditManager;
import com.mybatisflex.core.audit.ConsoleMessageCollector;
import com.mybatisflex.core.audit.MessageCollector;
import com.mybatisflex.core.datasource.DataSourceDecipher;
import com.mybatisflex.core.mybatis.FlexConfiguration;
import com.mybatisflex.core.query.QueryColumnBehavior;
import com.mybatisflex.spring.boot.ConfigurationCustomizer;
import com.mybatisflex.spring.boot.MyBatisFlexCustomizer;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * MyBatis-Flex 配置类
 * <p>
 * 配置 MyBatis-Flex ORM 框架，包括：
 * - Mapper 扫描路径配置
 * - 全局数据填充监听器
 * - SQL 审计和打印功能
 * - 数据源加密解密
 * </p>
 *
 * @author dataprince数据小王子
 * @Date 2026-01-15
 * @Wechat liiayy
 * @Email 773582348@qq.com
 * @Copyright <a href="https://code.muziseo.cn">木子软件</a>
 */
@EnableTransactionManagement(proxyTargetClass = true)
@AutoConfiguration
@Slf4j
@MapperScan(value = "${start.info.entity-package}", annotationClass = Mapper.class)
@PropertySource(value = "classpath:common-db.yml", factory = YmlPropertySourceFactory.class)
public class MyBatisFlexConfig implements ConfigurationCustomizer, MyBatisFlexCustomizer {

    static {
        QueryColumnBehavior.setIgnoreFunction(QueryColumnBehavior.IGNORE_BLANK);
        QueryColumnBehavior.setSmartConvertInToEquals(true);
    }

    /**
     * 是否启用 SQL 审计功能
     */
    @Value("${mybatis-flex.audit_enable:false}")
    private Boolean enableAudit = false;
    /**
     * 是否打印 SQL 语句到控制台
     */
    @Value("${mybatis-flex.sql_print:false}")
    private Boolean sqlPrint = false;

    /**
     * 配置数据源解密器
     * <p>
     * 用于解密配置文件中加密的数据源密码
     * </p>
     *
     * @return 数据源解密器
     */
    @Bean
    public DataSourceDecipher decipher() {
        return new Decipher();
    }

    /**
     * 自定义 MyBatis 配置
     * <p>
     * 配置 SQL 日志打印，便于开发调试
     * </p>
     *
     * @param configuration MyBatis-Flex 配置对象
     */
    @Override
    public void customize(FlexConfiguration configuration) {
        // mybatis实现的打印详细sql及返回结果到控制台，便于调试
        if (Boolean.TRUE.equals(sqlPrint)) {
            configuration.setLogImpl(StdOutImpl.class);
        }
    }

    /**
     * 自定义 MyBatis-Flex 全局配置
     * <p>
     * 配置内容包括：
     * - 注册全局实体监听器（插入/更新）
     * - 启用 SQL 审计功能
     * - 配置 SQL 打印
     * </p>
     *
     * @param globalConfig MyBatis-Flex 全局配置对象
     */
    @Override
    public void customize(FlexGlobalConfig globalConfig) {
        // 注册全局数据填充监听器
        // 注册为全局监听器，在监听器内部判断类型
        globalConfig.registerInsertListener(new EntityInsertListener());
        globalConfig.registerUpdateListener(new EntityUpdateListener());

        // 开启审计功能
        AuditManager.setAuditEnable(enableAudit);
        if (Boolean.TRUE.equals(sqlPrint)) {
            // 开启sql打印默认会开启sql审计
            AuditManager.setAuditEnable(true);
            // 设置 SQL 审计收集器
            MessageCollector collector = new ConsoleMessageCollector();
            AuditManager.setMessageCollector(collector);
        }
    }

}

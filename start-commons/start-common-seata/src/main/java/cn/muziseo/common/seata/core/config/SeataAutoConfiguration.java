package cn.muziseo.common.seata.core.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;

/**
 * Seata 分布式事务自动配置
 *
 * @author 木子软件
 */
@Slf4j
@AutoConfiguration
public class SeataAutoConfiguration {

    public SeataAutoConfiguration() {
        log.info("[StartAdmin] Seata 分布式事务公共组件已加载");
    }

}

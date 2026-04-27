# ===================================================================
# Nacos 配置模板
# ===================================================================
# 使用方法：
#   1. 登录 Nacos 控制台：http://192.168.100.20:8848/nacos
#   2. 进入「配置管理」→「配置列表」
#   3. 点击「+」新建配置，填入对应的 Data ID 和 Group
#   4. 配置格式选择 YAML，粘贴对应模板内容
#   5. 发布配置
#
# 命名规则：{spring.application.name}-{profile}.yaml
# Group: DEFAULT_GROUP
# 命名空间: public（开发）/ test / pre / prod
# ===================================================================


## ─────────────────────────────────────────────────────────
## Data ID: system-service-dev.yaml
## Group: DEFAULT_GROUP
## ─────────────────────────────────────────────────────────

spring:
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    driver-class-name: org.postgresql.Driver
    url: jdbc:postgresql://192.168.100.20:5432/start-admin-system?characterEncoding=UTF-8
    username: root
    password: root
  data:
    redis:
      host: 192.168.100.20
      port: 6379
      password: WeEvBhLapEHJMG4JFfFG
      database: 0

seata:
  application-name: ${spring.application.name}
  # 事务分组名称
  tx-service-group: my_test_tx_group
  # 代理模式：AT (推荐，需 undo_log 表) / XA (强一致性)
  data-source-proxy-mode: AT
  registry:
    type: nacos
    nacos:
      server-addr: 192.168.100.20:8848
      group: DEFAULT_GROUP
      # 对应 Seata Server 在 Nacos 注册的服务名
      application: seata-server
  config:
    type: nacos
    nacos:
      server-addr: 192.168.100.20:8848
      # 注意：此处通常为 Seata Server 自身的配置命名空间
      namespace: seata-server
      group: DEFAULT_GROUP
      data-id: seataServer.properties


## ─────────────────────────────────────────────────────────
## Data ID: demo-service-dev.yaml
## Group: DEFAULT_GROUP
## ─────────────────────────────────────────────────────────

spring:
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    driver-class-name: org.postgresql.Driver
    url: jdbc:postgresql://192.168.100.20:5432/demo?characterEncoding=UTF-8
    username: root
    password: root
  data:
    redis:
      host: 192.168.100.20
      port: 6379
      password: WeEvBhLapEHJMG4JFfFG
      database: 0

seata:
  application-name: ${spring.application.name}
  tx-service-group: my_test_tx_group
  data-source-proxy-mode: AT
  registry:
    type: nacos
    nacos:
      server-addr: 192.168.100.20:8848
      group: DEFAULT_GROUP
      application: seata-server
  config:
    type: nacos
    nacos:
      server-addr: 192.168.100.20:8848
      namespace: seata-server
      group: DEFAULT_GROUP
      data-id: seataServer.properties

logging:
  level:
    root: INFO
    org.springframework.web: DEBUG
    org.mybatis: DEBUG

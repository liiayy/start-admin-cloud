<h1 align="center">🚀 Start-Admin-Cloud 微服务后台管理框架</h1>

<p align="center">
  <b>基于 Spring Cloud Alibaba + MyBatis-Flex + Sa-Token 打造的现代化微服务开发脚手架</b>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/JDK-17+-green.svg" alt="JDK 17">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.x-blue.svg" alt="Spring Boot">
  <img src="https://img.shields.io/badge/Spring%20Cloud-2023.x-blue.svg" alt="Spring Cloud">
  <img src="https://img.shields.io/badge/MyBatis--Flex-1.7+-red.svg" alt="MyBatis-Flex">
  <img src="https://img.shields.io/badge/Sa--Token-1.37+-orange.svg" alt="Sa-Token">
  <img src="https://img.shields.io/badge/License-MIT-blue.svg" alt="License">
</p>

---

> 🌐 **在线演示**：[https://start-admin.p.muziseo.cn/](https://start-admin.p.muziseo.cn/)

## 📖 项目简介

**Start-Admin-Cloud** 是一款面向企业级的现代化微服务后台管理脚手架。项目采用业界最前沿的技术栈，基于 `Spring Boot 3.x`、`Spring Cloud Alibaba`、`MyBatis-Flex` 和 `Sa-Token` 构建，旨在为开发者提供一个**开箱即用、高内聚低耦合、规范统一**的微服务基础设施。

通过丰富的 `start-commons` 公共组件库，项目将缓存、多租户、分布式锁、日志审计、OSS、Excel处理、WebSocket 集群推送等常用能力进行了深度封装。让业务开发只需关注核心逻辑，拒绝重复造轮子。

## ✨ 核心特性

- 💡 **前沿技术栈**：全面拥抱 Java 21 与 Spring Boot 3.x，享受新特性带来的性能红利。
- 🛡️ **极致的权限控制**：基于轻量级、全能型的 `Sa-Token` 框架，提供完整的 RBAC 权限模型、细粒度的接口权限控制与分布式 Session 管理。
- ⚡ **高效持久层**：采用 `MyBatis-Flex` 作为 ORM 框架，以极其优雅的链式 API 极大地提升数据库操作效率。
- 🧩 **模块化极致解耦**：精心设计的 `start-commons` 组件包（涵盖 Cache、DB、Log、OSS、MQ 等 15+ 模块），按需引入，杜绝臃肿。
- 📡 **分布式能力拉满**：原生集成 Nacos（注册/配置中心）、Sentinel（熔断限流）、Seata（分布式事务）、SnailJob（分布式任务）。
- 🔌 **集群全双工通信**：内置经过分布式优化的 WebSocket 组件，支持跨节点/多端点/多设备的消息精准路由与会话管理。
- 📝 **极客级规范约束**：内置完善的代码开发规范、全局异常处理、字典与系统参数常量化管理，强迫症福音。

## 🛠️ 技术栈清单

| 技术分类 | 框架与组件 | 说明 |
| --- | --- | --- |
| **基础架构** | Spring Boot 3.x, Spring Cloud 2023.x | 微服务核心底座 |
| **服务治理** | Spring Cloud Alibaba (Nacos, Sentinel, Seata) | 注册中心、配置中心、限流熔断、分布式事务 |
| **网关路由** | Spring Cloud Gateway | 动态路由、全局鉴权、跨域与限流 |
| **持久化层** | MyBatis-Flex, Druid | ORM、连接池 |
| **认证授权** | Sa-Token | 登录认证、权限校验、单点登录 |
| **缓存中间件** | Redis, Redisson, Caffeine | 分布式缓存、一二级缓存架构、分布式锁 |
| **消息与任务** | RabbitMQ / RocketMQ, SnailJob | 异步解耦、分布式定时任务 |
| **其他工具** | MinIO/阿里云OSS, Hutool, MapStruct | 对象存储、基础工具类、对象映射 |

## 🏗️ 模块架构

```text
start-admin-cloud
├── start-dependencies         # 全局依赖版本管理 (BOM)
├── start-commons              # 公共能力组件 (高度解耦，业务微服务按需引入)
│   ├── start-common-cache     # 缓存模块 (Redis工具、Caffeine一二级缓存、Redisson锁)
│   ├── start-common-core      # 核心工具类 (全局异常、通用DTO、CacheConstants等常量)
│   ├── start-common-db        # 数据库组件 (MyBatis-Flex配置、多租户、数据权限)
│   ├── start-common-env       # 环境配置模块
│   ├── start-common-excel     # Excel 导入导出
│   ├── start-common-http      # HTTP 客户端封装
│   ├── start-common-job       # 分布式定时任务 (SnailJob)
│   ├── start-common-log       # 行为审计日志 (注解式操作日志记录)
│   ├── start-common-mq        # 消息队列组件
│   ├── start-common-oss       # 对象存储 (MinIO/阿里OSS/腾讯COS 统一抽象)
│   ├── start-common-satoken   # 认证授权配置 (网关鉴权/微服务鉴权同步)
│   ├── start-common-seata     # 分布式事务
│   ├── start-common-sentinel  # 熔断限流配置
│   ├── start-common-web       # Web 通用能力 (全局响应包装、统一异常拦截)
│   └── start-common-websocket # 集群式 WebSocket 实时推送
├── start-gateway              # API 微服务网关
├── start-service-api          # 内部 RPC API 接口定义包 (Feign Client)
│   └── start-service-system-api
└── start-services             # 业务微服务集群
    ├── start-service-system   # 系统基础微服务 (RBAC、字典、参数、日志管理)
    ├── start-service-job      # 任务调度微服务
    ├── start-service-demo     # 示例微服务 1
    └── start-service-demo2    # 示例微服务 2
```

## 🚀 快速开始

### 1. 环境准备
- **JDK**: 17+
- **Maven**: 3.8+
- **MySQL**: 8.0+
- **Redis**: 6.0+
- **Nacos**: 2.x

### 2. 获取代码与构建
```bash
git clone https://gitee.com/liiayy/start-admin-cloud.git
cd start-admin-cloud

# 编译并安装依赖到本地仓库
mvn clean install -DskipTests
```

### 3. 初始化基础设施
1. 创建 MySQL 数据库 `start_admin` 并导入基础 SQL 脚本。
2. 启动 Redis 与 Nacos 服务。
3. 在 Nacos 中导入项目的公共配置文件。

### 4. 启动服务
请务必按照以下顺序启动：
1. **API 网关**：`start-gateway` 模块的 `StartGatewayApplication`
2. **系统服务**：`start-service-system` 模块的 `SystemApplication` (提供核心权限与基础数据)
3. **其他业务服务**：根据实际业务需要启动

## 📐 开发规范与范式

本项目有着极高的代码质量追求，开发过程中请遵循：

- **统一字典/参数缓存**：摒弃魔法值，统一在 `CacheConstants`、`DictConstants` 等核心常量类中管理 Redis 键和常量。
- **强制 Javadoc**：所有公开接口与核心逻辑必须包含清晰的 JavaDoc。
- **领域分层模型**：
  - `Controller`：只做参数校验与视图分发。
  - `Service`：核心业务编排与规则处理。
  - `Manager`：通用业务处理逻辑、缓存集成、第三方 API 封装。
  - `Mapper/Entity`：纯粹的数据库交互映射。
  - `API/DTO`：微服务间调用的数据传输对象，严格与内部实体隔离。

## 🤝 参与贡献

我们非常欢迎开发者参与建设此项目！
1. Fork 本仓库
2. 新建 `Feat_xxx` 分支
3. 提交代码 (Commit message 请遵循 Angular 规范)
4. 新建 Pull Request

> 提交 PR 前请确保：代码规范整洁、新增功能有完整的注释说明。

## 📄 许可证

本项目开源协议基于 [MIT License](LICENSE)。您可以自由地使用、修改并分发，请保留作者版权信息。


# Start-Admin-Cloud

Start-Admin-Cloud 是一个基于 Spring Cloud + Spring Cloud Alibaba 的微服务后台管理系统，采用模块化架构设计，提供完整的微服务开发框架。

## 项目简介

Start-Admin-Cloud 致力于为开发者提供一个快速构建微服务应用的脚手架，集成业界主流技术栈，包含完整的认证授权、API 网关、服务注册发现、分布式事务、缓存、消息队列等能力。

## 技术栈

### 核心技术
- **Spring Cloud**: 微服务架构基础
- **Spring Cloud Gateway**: API 网关
- **Spring Cloud Alibaba**: 微服务组件集
- **Nacos**: 服务注册与配置中心
- **MyBatis-Flex**: ORM 持久层框架

### 认证与安全
- **Sa-Token**: 轻量级认证授权框架
- **Redisson**: 分布式锁
- **XSS**: Web 安全防护

### 分布式能力
- **Seata**: 分布式事务解决方案
- **Sentinel**: 流量控制与熔断
- **RabbitMQ**: 消息队列

### 其他
- **Redis**: 缓存中间件
- **SnailJob**: 分布式定时任务
- **Druid**: 数据库连接池

## 模块架构

```
start-admin-cloud
├── start-commons          # 公共模块
│   ├── start-common-cache     # 缓存模块 (Redis/Redisson)
│   ├── start-common-core      # 核心工具类
│   ├── start-common-db       # 数据库配置
│   ├── start-common-env      # 环境配置
│   ├── start-common-job      # 定时任务
│   ├── start-common-mq       # 消息队列
│   ├── start-common-satoken  # 认证授权
│   ├── start-common-seata    # 分布式事务
│   ├── start-common-sentinel # 熔断限流
│   └── start-common-web      # Web 公共组件
├── start-dependencies    # 依赖版本管理
├── start-gateway         # API 网关
├── start-service-api    # API 接口定义
│   └── start-service-system-api
└── start-services       # 业务服务
    ├── start-service-demo      # 示例服务
    ├── start-service-demo2     # 示例服务2
    ├── start-service-job       # 定时任务服务
    └── start-service-system    # 系统服务
```

## 核心功能

### 认证授权
- 基于 Sa-Token 的 token 认证
- 支持 JWT 模式
- 完整的 RBAC 权限模型（用户、角色、菜单、部门、岗位）

### API 网关
- 动态路由配置
- 请求限流控制
- 跨域配置
- 全局异常处理

### 缓存与锁
- Redis 缓存操作封装
- 分布式锁（可重入锁、公平锁、读写锁）
- 限流器

### Web 安全
- XSS 攻击防护
- 全局异常处理
- 请求参数验证

## 快速开始

### 环境要求
- JDK 17+
- Maven 3.8+
- Redis
- Nacos
- MySQL

### 构建项目

```bash
# 克隆项目
git clone https://gitee.com/liiayy/start-admin-cloud.git

# 构建所有模块
mvn clean install -DskipTests
```

### 启动顺序

1. **启动 Nacos**：服务注册与配置中心
2. **启动 Redis**：缓存与分布式锁
3.**：API **启动 Gateway 网关
4. **启动 System Service**：系统服务（用户、角色、菜单等）
5. **启动其他服务**：根据需求启动

### 配置说明

主要配置文件位于各模块的 `src/main/resources/` 目录下：
- `application.yaml`：主配置文件
- `common-*.yml`：公共配置（Nacos 中管理）
- `dev/application-dev.yaml`：开发环境配置

## 项目规范

项目遵循以下开发规范：
- 代码规范文档：`.trae/rules/project_rules.md`
- 模块开发范式：`.trae/rules/module_development_paradigm.md`

### 分层架构
- **Controller**: 请求入口参数校验
- **Service**: 业务逻辑接口
，- **ServiceImpl**: 业务逻辑实现
- **Manager**: 数据操作层
- **Mapper**: 数据持久层
- **Entity**: 实体类
- **API**: 远程调用接口

## 目录结构说明

```
cn.muziseo
├── common              # 公共模块
│   ├── cache           # 缓存
│   ├── core            # 核心工具
│   ├── db              # 数据库
│   ├── env             # 环境
│   ├── job             # 任务
│   ├── mq              # 消息
│   ├── satoken         # 认证
│   ├── seata           # 分布式事务
│   ├── sentinel        # 熔断
│   └── web             # Web
├── gateway             # 网关
├── service             # 业务服务
│   ├── system          # 系统服务
│   └── demo            # 示例
└── ...
```

## 贡献指南

欢迎提交 Pull Request，请确保遵循项目的代码规范。

## 许可证

本项目基于 LICENSE 开源协议。
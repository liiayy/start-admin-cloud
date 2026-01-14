# start-dependencies 模块说明

## 1. 模块简介

`start-dependencies` 是 StartAdmin 项目的依赖管理模块，使用 Maven BOM (Bill of Materials) 方式统一管理整个项目的依赖版本。

- **作用**：集中管理项目依赖版本，确保版本一致性，简化依赖配置
- **类型**：Maven POM 模块（packaging: pom）
- **适用范围**：项目内所有模块

## 2. 功能特性

- 统一管理 Spring Boot 生态依赖版本
- 统一管理第三方库依赖版本
- 统一管理项目内部模块版本
- 提供版本升级的集中控制点

## 3. 依赖管理列表

| 依赖名称 | 版本 | 说明 |
|---------|------|------|
| Spring Boot | 3.5.9 | 基础框架 |
| Spring Cloud | 2025.0.1 | 微服务框架 |
| Spring Cloud Alibaba | 2025.0.0.0 | 阿里巴巴微服务生态 |
| Hutool | 5.8.43 | Java 工具库 |
| start-common-core | 1.0.0 | 项目内部核心模块 |
| SpringDoc OpenAPI | 3.0.1 | API 文档生成 |

## 4. 使用方法

### 4.1 项目模块引用

在其他模块的 pom.xml 文件中，通过以下方式引用此 BOM：

```xml
<project>
    <!-- 父模块配置 -->
    <parent>
        <groupId>cn.muziseo</groupId>
        <artifactId>start-dependencies</artifactId>
        <version>1.0.0</version>
        <relativePath>../start-dependencies/pom.xml</relativePath>
    </parent>
    
    <!-- 依赖配置（无需指定版本） -->
    <dependencies>
        <!-- 例如：引用 Hutool 工具库 -->
        <dependency>
            <groupId>cn.hutool</groupId>
            <artifactId>hutool-all</artifactId>
        </dependency>
        
        <!-- 例如：引用项目内部核心模块 -->
        <dependency>
            <groupId>cn.muziseo</groupId>
            <artifactId>start-common-core</artifactId>
        </dependency>
    </dependencies>
</project>
```

### 4.2 版本管理说明

- 所有依赖版本在 `properties` 节点中定义
- 版本号使用变量形式，便于统一修改
- 核心版本号 `revision` 统一控制项目内部模块版本

## 5. 版本变更记录

| 版本 | 变更内容 | 日期 | 备注 |
|------|---------|------|------|
| 1.0.0 | 初始化模块，配置基础依赖版本 | 2026-01-13 | 初始版本 |

## 6. 相关文档

- [什么是 BOM](https://code.muziseo.cn/archives/shi-me-shi-bom)
- [Maven 依赖管理最佳实践](https://code.muziseo.cn/archives/maven-yi-lai-guan-li-zui-jia-shi-jian)

## 7. 注意事项

1. **版本一致性**：所有模块必须通过此 BOM 管理依赖版本，避免版本冲突
2. **升级规范**：依赖版本升级需在本模块统一修改，确保全项目同步
3. **依赖范围**：仅管理版本，不强制引入依赖，各模块根据需要自行引入
4. **兼容性**：升级依赖版本时需验证兼容性，尤其是 Spring Boot 与其他框架的版本匹配

## 8. 维护指南

### 8.1 如何添加新依赖

1. 在 `properties` 节点中添加版本变量
2. 在 `dependencyManagement` 节点中添加依赖配置
3. 更新 README.md 中的依赖管理列表

### 8.2 如何升级依赖版本

1. 修改 `properties` 节点中对应的版本变量
2. 验证升级后项目是否正常构建和运行
3. 更新 README.md 中的版本变更记录

## 9. 联系方式

- **官方网站**：https://code.muziseo.cn
- **技术文档**：https://code.muziseo.cn/archives
- **问题反馈**：https://code.muziseo.cn/issues
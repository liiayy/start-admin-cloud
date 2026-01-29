# StartAdmin 业务模块开发范式文档

## 1. 概述

本文档基于 demo 业务模块，定义了 StartAdmin 项目中业务模块的开发范式和规范。所有新开发的业务模块必须严格遵循本规范，以确保代码的一致性、可维护性和可扩展性。

## 2. 目录结构规范

每个业务模块应按照以下标准目录结构进行组织：

```
module/
  ├── 模块名/
  │   ├── api/                    # API 接口实现层（用于 Feign 调用）
  │   │   └── 模块名ApiImpl.java
  │   ├── controller/             # 控制器层
  │   │   └── 模块名Controller.java
  │   ├── manager/                # 业务管理层（数据访问管理）
  │   │   └── 模块名Manager.java
  │   ├── repository/             # 数据访问层
  │   │   ├── entity/            # 实体类
  │   │   │   └── 模块名Entity.java
  │   │   └── mapper/            # MyBatis Mapper 接口
  │   │       └── 模块名Mapper.java
  │   └── service/               # 服务层
  │       ├── 模块名Service.java
  │       └── impl/
  │           └── 模块名ServiceImpl.java
```

### 2.1 目录说明

- **api/**: API 接口实现层，用于提供 Feign 调用接口，实现外部服务调用的 API 定义
- **controller/**: 控制器层，处理 HTTP 请求和响应，负责接收前端请求并返回数据
- **manager/**: 业务管理层，继承 MyBatis-Plus 的 ServiceImpl，负责数据访问和基础 CRUD 操作
- **repository/**: 数据访问层，包含实体类和 Mapper 接口
    - **entity/**: 数据库实体类，对应数据库表结构
    - **mapper/**: MyBatis Mapper 接口，定义数据库操作方法
- **service/**: 服务层，定义业务逻辑接口和实现类
    - **impl/**: 服务实现类，实现具体的业务逻辑

## 3. 命名规范

### 3.1 包命名

- 包名使用域名倒置的方式，遵循小写字母规范
- 格式：`cn.muziseo.service.system.module.模块名.子模块名`
- 示例：`cn.muziseo.service.system.module.demo.service`

### 3.2 类命名

- **Entity 类**：使用大驼峰命名法，以 `Entity` 结尾
    - 示例：`DemoEntity`
- **Mapper 接口**：使用大驼峰命名法，以 `Mapper` 结尾
    - 示例：`DemoMapper`
- **Manager 类**：使用大驼峰命名法，以 `Manager` 结尾
    - 示例：`DemoManager`
- **Service 接口**：使用大驼峰命名法，以 `Service` 结尾
    - 示例：`DemoService`
- **Service 实现类**：使用大驼峰命名法，以 `ServiceImpl` 结尾
    - 示例：`DemoServiceImpl`
- **Controller 类**：使用大驼峰命名法，以 `Controller` 结尾
    - 示例：`DemoController`
- **API 实现类**：使用大驼峰命名法，以 `ApiImpl` 结尾
    - 示例：`DemoApiImpl`

### 3.3 方法命名

- 查询方法使用 `get`、`query`、`find` 等前缀
- 新增方法使用 `add`、`create`、`insert` 等前缀
- 更新方法使用 `update` 前缀
- 删除方法使用 `delete`、`remove` 前缀
- 示例：
    - `getAll()` - 获取全部数据
    - `addDemo()` - 添加数据
    - `updateDemo()` - 更新数据
    - `deleteDemo()` - 删除数据

## 4. 分层架构规范

### 4.1 Controller 层

**职责**：处理 HTTP 请求，接收参数，调用 Service 层，返回响应

**规范**：

- 使用 `@RestController` 注解标记为 REST 控制器
- 使用 `@RequestMapping` 或 `@GetMapping`、`@PostMapping` 等定义请求路径
- 使用 `@Resource` 注入 Service
- 方法参数使用 `HttpServletRequest` 和 `HttpServletResponse` 时，按顺序放置
- 不包含业务逻辑，仅负责请求分发和响应封装

**示例**：

```java
package cn.muziseo.service.system.module.demo.controller;

import cn.muziseo.service.system.module.demo.repository.entity.DemoEntity;
import cn.muziseo.service.system.module.demo.service.DemoService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/demo")
public class DemoController {

    @Resource
    DemoService demoService;

    @GetMapping("/test")
    public String test(HttpServletRequest request, HttpServletResponse response) {
        List<DemoEntity> all = demoService.getAll();
        return "Hello word";
    }
}
```

### 4.2 Service 层

**职责**：定义业务逻辑接口，提供业务方法声明

**规范**：

- 使用 `interface` 定义接口
- 方法添加 Javadoc 注释，说明功能、参数和返回值
- 方法命名清晰表达业务含义

**示例**：

```java
package cn.muziseo.service.system.module.demo.service;

import cn.muziseo.service.system.module.demo.repository.entity.DemoEntity;

import java.util.List;

public interface DemoService {
    /**
     * 获取全部
     *
     * @return Demo列表
     */
    List<DemoEntity> getAll();
}
```

### 4.3 ServiceImpl 层

**职责**：实现 Service 接口，实现具体业务逻辑

**规范**：

- 使用 `@Service` 注解标记为服务类
- 实现 Service 接口
- 使用 `@Resource` 注入 Manager
- 调用 Manager 层进行数据访问
- 包含业务逻辑处理

**示例**：

```java
package cn.muziseo.service.system.module.demo.service.impl;

import cn.muziseo.service.system.module.demo.manager.DemoManager;
import cn.muziseo.service.system.module.demo.repository.entity.DemoEntity;
import cn.muziseo.service.system.module.demo.service.DemoService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DemoServiceImpl implements DemoService {

    @Resource
    DemoManager demoManager;

    @Override
    public List<DemoEntity> getAll() {
        return demoManager.list();
    }
}
```

### 4.4 Manager 层

**职责**：数据访问管理，继承 MyBatis-Plus 的 ServiceImpl，提供基础 CRUD 操作

**规范**：

- 使用 `@Service` 注解标记为服务类
- 继承 `ServiceImpl<Mapper, Entity>`
- 泛型参数：第一个为对应的 Mapper 接口，第二个为对应的 Entity 类
- 可添加自定义的数据访问方法

**示例**：

```java
package cn.muziseo.service.system.module.demo.manager;

import cn.muziseo.service.system.module.demo.repository.entity.DemoEntity;
import cn.muziseo.service.system.module.demo.repository.mapper.DemoMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class DemoManager extends ServiceImpl<DemoMapper, DemoEntity> {

}
```

### 4.5 Mapper 层

**职责**：定义数据库操作接口

**规范**：

- 使用 `@Mapper` 注解标记为 MyBatis Mapper
- 继承 `BaseMapper<Entity>`
- 泛型参数为对应的 Entity 类
- 可添加自定义的数据库操作方法

**示例**：

```java
package cn.muziseo.service.system.module.demo.repository.mapper;

import cn.muziseo.service.system.module.demo.repository.entity.DemoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DemoMapper extends BaseMapper<DemoEntity> {

}
```

### 4.6 Entity 层

**职责**：定义数据库实体类，对应数据库表结构

**规范**：

- 使用 `@TableName` 注解指定数据库表名
- 使用 `@TableId` 注解标记主键字段
- 使用 Lombok 注解简化代码：
    - `@Data` - 自动生成 getter/setter
    - `@EqualsAndHashCode(callSuper = true)` - 继承父类的 equals/hashCode
    - `@ToString(callSuper = true)` - 继承父类的 toString
    - `@Builder` - 支持构建者模式
    - `@NoArgsConstructor` - 无参构造器
    - `@AllArgsConstructor` - 全参构造器
- 继承 `BaseEntity` 获得公共字段（创建时间、更新时间、创建者、更新者、删除标记）

**示例**：

```java
package cn.muziseo.service.system.module.demo.repository.entity;

import cn.muziseo.common.db.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@TableName("demo")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DemoEntity extends BaseEntity {

    @TableId
    private Long id;

    private String name;
}
```

### 4.7 API 层

**职责**：实现 Feign API 接口，提供远程服务调用

**规范**：

- 使用 `@RestController` 注解标记为 REST 控制器
- 实现 API 接口（通常在 api 模块中定义）
- 提供远程调用的具体实现

**示例**：

```java
package cn.muziseo.service.system.module.demo.api;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoApiImpl implements DemoApi {
    @Override
    public String demo() {
        return "测试一下,看看如何";
    }
}
```

## 5. 注解使用规范

### 5.1 Spring 注解

- `@RestController` - 标记 REST 控制器
- `@RequestMapping` - 定义请求路径
- `@GetMapping` - 定义 GET 请求
- `@PostMapping` - 定义 POST 请求
- `@PutMapping` - 定义 PUT 请求
- `@DeleteMapping` - 定义 DELETE 请求
- `@Service` - 标记服务类
- `@Resource` - 依赖注入（项目推荐使用）

### 5.2 MyBatis-Plus 注解

- `@TableName("表名")` - 指定数据库表名
- `@TableId` - 标记主键字段
- `@TableField` - 标记普通字段，可配置字段填充策略
- `@TableLogic` - 标记逻辑删除字段

### 5.3 Lombok 注解

- `@Data` - 自动生成 getter/setter、toString、equals、hashCode
- `@EqualsAndHashCode(callSuper = true)` - 继承父类的 equals/hashCode
- `@ToString(callSuper = true)` - 继承父类的 toString
- `@Builder` - 支持构建者模式
- `@NoArgsConstructor` - 生成无参构造器
- `@AllArgsConstructor` - 生成全参构造器

### 5.4 Jakarta 注解

- `jakarta.annotation.Resource` - 依赖注入
- `jakarta.servlet.http.HttpServletRequest` - HTTP 请求对象
- `jakarta.servlet.http.HttpServletResponse` - HTTP 响应对象

## 6. 依赖注入规范

- 优先使用 `@Resource` 注解进行依赖注入
- 注入对象声明为接口类型，而非具体实现类
- 示例：

```java
@Resource
DemoService demoService;
```

## 7. BaseEntity 基类规范

所有 Entity 类必须继承 `BaseEntity`，以获得以下公共字段：

- `createTime` - 创建时间（插入时自动填充）
- `updateTime` - 最后更新时间（插入和更新时自动填充）
- `creator` - 创建者（插入时自动填充）
- `updater` - 更新者（插入和更新时自动填充）
- `deleted` - 是否删除（逻辑删除标记）

## 8. 开发流程

### 8.1 新建业务模块步骤

1. **创建目录结构**：按照第 2 节的目录结构规范创建目录
2. **创建 Entity 类**：定义数据库实体类，继承 BaseEntity
3. **创建 Mapper 接口**：继承 BaseMapper<Entity>
4. **创建 Manager 类**：继承 ServiceImpl<Mapper, Entity>
5. **创建 Service 接口**：定义业务方法
6. **创建 ServiceImpl 类**：实现 Service 接口
7. **创建 Controller 类**：定义 HTTP 接口
8. **创建 API 实现类**（如需要）：实现 Feign API 接口

### 8.2 开发顺序建议

1. 先设计数据库表结构
2. 创建 Entity 类
3. 创建 Mapper 接口
4. 创建 Manager 类
5. 创建 Service 接口
6. 创建 ServiceImpl 类
7. 创建 Controller 类
8. 测试接口功能

## 9. 代码示例

### 9.1 完整模块示例

以下是一个完整的 demo 模块代码示例：

**Entity 类**：

```java
package cn.muziseo.service.system.module.demo.repository.entity;

import cn.muziseo.common.db.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@TableName("demo")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DemoEntity extends BaseEntity {

    @TableId
    private Long id;

    private String name;
}
```

**Mapper 接口**：

```java
package cn.muziseo.service.system.module.demo.repository.mapper;

import cn.muziseo.service.system.module.demo.repository.entity.DemoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DemoMapper extends BaseMapper<DemoEntity> {

}
```

**Manager 类**：

```java
package cn.muziseo.service.system.module.demo.manager;

import cn.muziseo.service.system.module.demo.repository.entity.DemoEntity;
import cn.muziseo.service.system.module.demo.repository.mapper.DemoMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class DemoManager extends ServiceImpl<DemoMapper, DemoEntity> {

}
```

**Service 接口**：

```java
package cn.muziseo.service.system.module.demo.service;

import cn.muziseo.service.system.module.demo.repository.entity.DemoEntity;

import java.util.List;

public interface DemoService {
    /**
     * 获取全部
     *
     * @return Demo列表
     */
    List<DemoEntity> getAll();
}
```

**Service 实现类**：

```java
package cn.muziseo.service.system.module.demo.service.impl;

import cn.muziseo.service.system.module.demo.manager.DemoManager;
import cn.muziseo.service.system.module.demo.repository.entity.DemoEntity;
import cn.muziseo.service.system.module.demo.service.DemoService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DemoServiceImpl implements DemoService {

    @Resource
    DemoManager demoManager;

    @Override
    public List<DemoEntity> getAll() {
        return demoManager.list();
    }
}
```

**Controller 类**：

```java
package cn.muziseo.service.system.module.demo.controller;

import cn.muziseo.service.system.module.demo.repository.entity.DemoEntity;
import cn.muziseo.service.system.module.demo.service.DemoService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/demo")
public class DemoController {

    @Resource
    DemoService demoService;

    @GetMapping("/test")
    public String test(HttpServletRequest request, HttpServletResponse response) {
        List<DemoEntity> all = demoService.getAll();
        return "Hello word";
    }
}
```

**API 实现类**：

```java
package cn.muziseo.service.system.module.demo.api;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoApiImpl implements DemoApi {
    @Override
    public String demo() {
        return "测试一下,看看如何";
    }
}
```

## 10. 注意事项

1. **严格遵循分层架构**：各层职责清晰，不要跨层调用
2. **使用接口编程**：Service 层使用接口，便于扩展和测试
3. **合理使用 MyBatis-Plus**：充分利用 MyBatis-Plus 提供的 CRUD 方法，减少重复代码
4. **注意命名规范**：类名、方法名、变量名要清晰表达含义
5. **添加必要注释**：公共方法必须添加 Javadoc 注释
6. **继承 BaseEntity**：所有 Entity 类必须继承 BaseEntity
7. **使用 Lombok**：合理使用 Lombok 注解简化代码
8. **使用 @Resource**：依赖注入使用 @Resource 注解

## 11. 总结

本开发范式文档基于 demo 业务模块，定义了 StartAdmin
项目中业务模块的开发规范。所有开发人员必须严格遵循本规范，以确保代码的一致性、可维护性和可扩展性。在开发过程中，如遇到本规范未覆盖的情况，应参考
demo 模块的实现方式，并与团队讨论确定最佳实践。

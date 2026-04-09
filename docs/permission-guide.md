# RBAC 权限体系设计文档

## 1. 整体架构

本项目采用 **RBAC（Role-Based Access Control）** 权限模型，基于 Sa-Token 实现认证与授权。

核心思路：**登录时写入 Session，鉴权时从 Session 读取**，通过 Redis 实现跨服务权限共享。

```
┌──────────────┐     登录写入 Session     ┌─────────┐
│ system-service │ ──────────────────────► │  Redis  │
│ (AuthServiceImpl)                        │         │
│ (SaSessionRefreshService)                │         │
└──────────────┘                           │         │
                                           │         │
┌──────────────┐     鉴权读取 Session     │         │
│ 任意微服务     │ ◄────────────────────── │         │
│ (satoken-integration)                    └─────────┘
└──────────────┘
```

### 模块职责

| 模块 | 职责 |
|------|------|
| `start-common-satoken` | Sa-Token 基础设施：JWT 配置、TokenDao（Redis+Caffeine）、异常处理、密码工具 |
| `start-common-satoken-integration` | 权限集成：`SaPermissionService` 接口 + Session 读取实现 + `StpInterface` 自动注册 |
| `start-service-system` | 权限管理：用户/角色/菜单 CRUD、登录认证、Session 写入与刷新 |

---

## 2. 数据库模型

### ER 关系

```
system_user ──1:N──► system_user_role ◄──N:1── system_role
                                                  │
                                                  1:N
                                                  │
                                                  ▼
                                          system_role_menu ◄──N:1── system_menu
```

### 表结构

#### system_user（用户表）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 用户ID，主键 |
| username | varchar(64) | 用户账号，唯一 |
| password | varchar(100) | BCrypt 加密密码 |
| nickname | varchar(30) | 昵称 |
| status | tinyint | 状态（0正常 1停用） |
| dept_id | bigint | 部门ID |
| mobile | varchar(11) | 手机号 |
| email | varchar(50) | 邮箱 |
| avatar | varchar(255) | 头像 |
| login_ip | varchar(128) | 最后登录IP |
| login_date | datetime | 最后登录时间 |

#### system_role（角色表）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 角色ID，主键 |
| name | varchar(30) | 角色名称 |
| code | varchar(100) | 角色编码，如 `admin`、`editor` |
| status | tinyint | 状态（0正常 1停用） |
| data_scope | int | 数据权限范围（预留） |

#### system_menu（菜单/权限表）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 菜单ID，主键 |
| name | varchar(50) | 菜单名称 |
| permission | varchar(100) | 权限标识，如 `system:user:create` |
| type | char(1) | 类型（M目录 C菜单 F按钮） |
| parent_id | bigint | 父菜单ID |
| path | varchar(200) | 路由地址 |
| component | varchar(255) | 组件路径 |
| icon | varchar(100) | 图标 |
| status | tinyint | 状态（0正常 1停用） |

#### system_user_role（用户角色关联表）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键 |
| user_id | bigint | 用户ID |
| role_id | bigint | 角色ID |

#### system_role_menu（角色菜单关联表）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键 |
| role_id | bigint | 角色ID |
| menu_id | bigint | 菜单ID |

---

## 3. 认证流程

### 3.1 登录

```
用户提交账号密码
      │
      ▼
┌─ 检查登录失败计数（Redis） ── 失败 >= 5次 ──► 拒绝，锁定15分钟
│
│  通过
│     │
│     ▼
┌─ 查询用户 + BCrypt 校验密码 ── 不匹配 ──► 递增失败计数，拒绝
│
│  通过
│     │
│     ▼
┌─ 检查用户状态 ── 已停用 ──► 拒绝
│
│  通过
│     │
│     ▼
   清除失败计数
       │
       ▼
   StpUtil.login(userId)          ← Sa-Token 登录
       │
       ▼
   SaSessionRefreshService        ← 查询角色/权限，写入 Session
   .refreshUserSession(userId)
       │
       ▼
   返回 Token（JWT）
```

### 3.2 密码安全

- 算法：BCrypt（`spring-security-crypto`）
- 工具类：`cn.muziseo.common.satoken.core.util.PasswordUtils`
  - `encode(rawPassword)` — 加密
  - `matches(rawPassword, encodedPassword)` — 验证
- 登录失败保护：同一账号 5 次失败后锁定 15 分钟（Redis key: `login_fail:{username}`）

### 3.3 登出

直接调用 `StpUtil.logout()`，Sa-Token 自动清理 Session 和 Token。

---

## 4. 权限加载与共享

### 4.1 Session 数据结构

登录后，用户角色和权限存储在 Sa-Token Session 中（底层为 Redis）：

```
SaSession (key: satoken:login:session:{userId})
├── "roles"       → Set<String>  如 {"admin", "editor"}
└── "permissions" → Set<String>  如 {"system:user:create", "system:role:query"}
```

### 4.2 写入端（system-service）

`SaSessionRefreshService` 负责将角色和权限写入 Session：

```
refreshUserSession(userId)
    │
    ├── RoleService.getRolesByUserId(userId)          → 角色列表
    │       └── 提取 roleCodes（角色编码集合）
    │       └── 提取 roleIds
    │
    ├── MenuService.getMenusByRoleIds(roleIds)         → 菜单列表
    │       └── 提取 permission（非空权限标识集合）
    │
    └── session.set("roles", roleCodes)
        session.set("permissions", permissions)
```

### 4.3 读取端（任意微服务）

微服务只需引入 `start-common-satoken-integration` 依赖，即可自动获得权限加载能力：

```xml
<dependency>
    <groupId>cn.muziseo</groupId>
    <artifactId>start-common-satoken-integration</artifactId>
</dependency>
```

`SaTokenIntegrationAutoConfiguration` 自动注册 `StpInterface` 实现，从 Session 读取：

```
StpInterface.getPermissionList(loginId)
    └── SaPermissionServiceImpl.getPermissionList(userId)
        └── StpUtil.getSessionByLoginId(userId)
            └── session.getModel("permissions", Set.class)

StpInterface.getRoleList(loginId)
    └── SaPermissionServiceImpl.getRoleCodes(userId)
        └── StpUtil.getSessionByLoginId(userId)
            └── session.getModel("roles", Set.class)
```

### 4.4 优势

| 对比项 | Session 共享模式 | 每次查库模式 | Feign 调用模式 |
|--------|----------------|-------------|---------------|
| 性能 | Redis 读取，毫秒级 | 每次查数据库 | 网络调用，延迟高 |
| 耦合度 | 各服务无依赖 | 各服务依赖相同表 | 各服务依赖 system-service |
| 可用性 | Redis 可用时即可 | 数据库可用时即可 | system-service 可用时才可 |
| 数据时效 | 变更后即时刷新 | 天然实时 | 天然实时 |

---

## 5. 权限刷新机制

当管理员修改权限相关数据时，必须同步刷新受影响用户的 Session，确保权限即时生效。

### 5.1 触发场景

| 操作 | 触发位置 | 刷新范围 |
|------|---------|---------|
| 用户登录 | `AuthServiceImpl.login()` | 当前登录用户 |
| 管理员分配用户角色 | `UserServiceImpl.assignRole()` | 被操作的用户 |
| 管理员修改角色菜单 | `RoleServiceImpl.assignMenus()` | 拥有该角色的所有用户 |

### 5.2 接入新场景

后续新增"停用角色"、"删除菜单"等操作时，在 Service 方法末尾调用：

```java
@Resource
private SaSessionRefreshService saSessionRefreshService;

// 单个用户
saSessionRefreshService.refreshUserSession(userId);

// 多个用户（如角色变更影响多人）
List<Long> userIds = userRoleManager.getUserIdsByRoleId(roleId);
saSessionRefreshService.refreshUserSessions(userIds);
```

---

## 6. 接口权限控制

### 6.1 权限标识命名规范

格式：`模块:功能:操作`

```
system:user:create      系统/用户/创建
system:user:update      系统/用户/更新
system:user:delete      系统/用户/删除
system:user:query       系统/用户/查询
system:role:assign      系统/角色/分配
```

### 6.2 使用注解鉴权

在 Controller 方法上使用 Sa-Token 注解：

```java
// 权限校验
@SaCheckPermission("system:user:create")
@PostMapping("/create")
public ResponseDTO<Void> createUser(@RequestBody @Valid UserAddRequest request) { ... }

// 角色校验
@SaCheckRole("admin")
@GetMapping("/page")
public ResponseDTO<PageResponse<UserVO>> pageUser(UserPageRequest request) { ... }
```

### 6.3 常用注解

| 注解 | 说明 |
|------|------|
| `@SaCheckPermission("x:y:z")` | 校验权限标识 |
| `@SaCheckRole("admin")` | 校验角色编码 |
| `@SaCheckLogin` | 校验是否登录 |
| `@SaCheckPermission(value = {"a", "b"}, mode = SaMode.AND)` | 同时拥有多个权限 |
| `@SaCheckPermission(value = {"a", "b"}, mode = SaMode.OR)` | 拥有任一权限 |

---

## 7. 关键文件索引

### start-common-satoken（基础设施）

```
src/main/java/cn/muziseo/common/satoken/core/
├── config/
│   └── SaTokenConfiguration.java        # JWT、TokenDao、异常处理器注册
├── dao/
│   └── StartSaTokenDao.java             # Redis + Caffeine 混合 TokenDao
├── handler/
│   └── SaTokenExceptionHandler.java     # 全局异常处理（未登录、无权限等）
└── util/
    └── PasswordUtils.java               # BCrypt 密码加密/验证
```

### start-common-satoken-integration（权限集成）

```
src/main/java/cn/muziseo/common/satoken/integration/
├── config/
│   └── SaTokenIntegrationAutoConfiguration.java  # 自动注册 StpInterface
└── service/
    ├── SaPermissionService.java                   # 接口：获取角色/权限
    └── SaPermissionServiceImpl.java               # 实现：从 Session 读取
```

### start-service-system（权限管理）

```
src/main/java/cn/muziseo/service/system/module/
├── auth/
│   ├── service/
│   │   ├── AuthService.java                       # 认证接口
│   │   ├── UserService.java                       # 用户接口
│   │   ├── SaSessionRefreshService.java           # Session 刷新服务
│   │   └── impl/
│   │       ├── AuthServiceImpl.java               # 登录/登出 + Session 写入
│   │       └── UserServiceImpl.java               # 用户 CRUD + 角色分配
│   ├── manager/
│   │   ├── UserManager.java                       # 用户数据访问
│   │   └── UserRoleManager.java                   # 用户角色关联数据访问
│   └── repository/
│       ├── entity/
│       │   ├── UserEntity.java
│       │   └── UserRoleEntity.java
│       └── mapper/
│           ├── UserMapper.java
│           └── UserRoleMapper.java
└── permission/
    ├── service/
    │   ├── RoleService.java                       # 角色接口
    │   ├── MenuService.java                       # 菜单接口
    │   └── impl/
    │       └── RoleServiceImpl.java               # 角色管理 + Session 刷新
    ├── manager/
    │   ├── RoleManager.java                       # 角色数据访问
    │   └── RoleMenuManager.java                   # 角色菜单关联数据访问
    └── repository/
        ├── entity/
        │   ├── RoleEntity.java
        │   ├── MenuEntity.java
        │   └── RoleMenuEntity.java
        └── mapper/
            ├── RoleMapper.java
            ├── MenuMapper.java
            └── RoleMenuMapper.java
```

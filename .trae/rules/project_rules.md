# StartAdmin 代码规范文档

## 1. 概述

本文档定义了 StartAdmin 项目的代码规范，旨在确保团队开发的代码具有一致性、可读性和可维护性。所有团队成员在开发过程中必须严格遵守本规范。

## 2. 命名规范

### 2.1 包命名

- 包名使用域名倒置的方式，遵循小写字母规范
- 格式：`cn.muziseo.模块名.子模块名...`
- 示例：`cn.muziseo.admin.module.system.employee.controller`

### 2.2 类命名

- 类名使用大驼峰命名法（PascalCase）
- 接口名也使用大驼峰命名法
- 抽象类命名以 `Abstract` 或 `Base` 开头
- 实现类命名通常为接口名+`Impl`
- 枚举类命名使用大驼峰，枚举值使用全大写加下划线
- 示例：
  - `EmployeeController`
  - `EmployeeService`
  - `EmployeeEntity`
  - `GenderEnum`
  - `BaseService`

### 2.3 方法命名

- 方法名使用小驼峰命名法（camelCase）
- 方法名应当准确描述其功能
- 查询方法使用 `query`、`get`、`find` 等前缀
- 更新方法使用 `update` 前缀
- 删除方法使用 `delete`、`remove` 前缀
- 添加方法使用 `add`、`create`、`insert` 前缀
- 示例：
  - `queryEmployee`
  - `addEmployee`
  - `updateDisableFlag`
  - `batchUpdateDepartment`

### 2.4 变量命名

- 变量名使用小驼峰命名法（camelCase）
- 成员变量和局部变量命名应具有描述性
- 布尔类型变量可以使用 `is`、`has`、`can` 等前缀
- 临时变量、循环变量等可以使用简单名称如 `i`、`j`、`temp` 等
- 示例：
  - `employeeId`
  - `loginName`
  - `disabledFlag`
  - `departmentIdList`

### 2.5 常量命名

- 常量使用全大写字母和下划线组合
- 静态常量使用 `static final` 修饰
- 常量应放置在专用的常量类中或类的顶部
- 示例：
  - `SYSTEM_EMPLOYEE`
  - `SWAGGER_WHITELIST`
  - `MAX_PAGE_SIZE`

### 2.6 文件名命名

- Java文件名与类名保持一致
- 配置文件名使用小驼峰或短横线分隔
- 资源文件名使用小写字母和下划线组合
- 示例：
  - `EmployeeController.java`
  - `application-dev.yaml`
  - `log4j2-spring.xml`

### 2.7 目录命名

- 目录名使用小写字母
- 遵循项目结构约定的目录名
- 示例：
  - `controller`
  - `service`
  - `mapper`
  - `domain`
  - `entity`
  - `form`
  - `vo`

## 3. 代码格式

### 3.1 缩进

- 使用 4 个空格进行缩进，不使用 Tab
- 统一 IDE 的缩进设置

### 3.2 括号使用

- 大括号 `{` 不单独占一行，与前面的代码在同一行
- 右大括号 `}` 单独占一行
- 条件语句、循环语句的代码块即使只有一行也必须使用大括号
- 示例：

```java
if (condition) {
    // 代码块
} else {
    // 代码块
}

for (int i = 0; i < count; i++) {
    // 代码块
}
```

### 3.3 空格规范

- 运算符前后各加一个空格
- 逗号、冒号、分号后加一个空格
- 方法参数列表中，逗号后加一个空格
- 括号内侧不加空格
- 示例：

```java
int result = a + b * c;
String name = "admin";
List<String> list = Arrays.asList("a", "b", "c");
```

### 3.4 换行规则

- 一行代码不应过长，建议不超过 120 个字符
- 长方法调用链可以在点号后换行，下一行缩进 8 个空格
- 长参数列表可以每行一个参数，垂直对齐
- 示例：

```java
// 长方法调用链换行
ResponseDTO<PageResult<EmployeeVO>> response = employeeService
        .queryEmployee(employeeQueryForm)
        .setPageSize(10)
        .setOrderBy("createTime");

// 长参数列表换行
public ResponseDTO<String> addEmployee(
        @Valid @RequestBody EmployeeAddForm employeeAddForm,
        HttpServletRequest request,
        HttpServletResponse response) {
    // 代码块
}
```

## 4. 语法规范

### 4.1 类和接口定义

- 类定义前添加文档注释
- 类成员变量应遵循封装原则，使用 `private` 修饰，提供 getter/setter 方法（或使用 Lombok）
- 接口方法应定义清晰的参数和返回值
- 使用 Lombok 的 `@Data` 注解简化实体类代码
- 示例：

```java
/**
 * 员工 实体表
 */
@Data
@TableName("t_employee")
public class EmployeeEntity {
    @TableId(type = IdType.AUTO)
    private Long employeeId;
    // 其他字段
}
```

### 4.2 方法定义

- 方法定义前添加注释说明功能
- 方法参数应有明确的类型和名称
- 返回值类型应明确，避免使用 `Object`
- 使用 `@Operation` 注解描述 REST API 的功能
- 使用 `@SaCheckPermission` 注解进行权限控制
- 示例：

```java
@Operation(summary = "添加员工(返回添加员工的密码) @author 李彦军")
@PostMapping("/employee/add")
@SaCheckPermission("system:employee:add")
public ResponseDTO<String> addEmployee(@Valid @RequestBody EmployeeAddForm employeeAddForm) {
    return employeeService.addEmployee(employeeAddForm);
}
```

### 4.3 异常处理

- 使用 `try-catch-finally` 处理异常
- 具体异常应优先于通用异常捕获
- 避免捕获 `Exception` 基类而不做任何处理
- 使用 `ResponseDTO` 统一封装API响应结果
- 示例：

```java
try {
    // 业务逻辑
    return ResponseDTO.ok(result);
} catch (BusinessException e) {
    log.error("业务异常: {}", e.getMessage());
    return ResponseDTO.error(e.getMessage());
} catch (Exception e) {
    log.error("系统异常: {}", e.getMessage(), e);
    return ResponseDTO.error(UserErrorCode.SYSTEM_ERROR);
}
```

### 4.4 集合操作

- 使用泛型定义集合
- 优先使用 `ArrayList`、`HashMap` 等具体实现类，避免使用 `Vector`、`Hashtable` 等线程安全但性能较差的集合
- 使用 Java 8+ 的 Stream API 进行集合操作
- 示例：

```java
// 正确的集合定义
List<String> nameList = new ArrayList<>();
Map<String, EmployeeVO> employeeMap = new HashMap<>();

// 使用Stream API
List<Long> employeeIdList = employeeList.stream()
        .map(EmployeeVO::getEmployeeId)
        .collect(Collectors.toList());
```

### 4.5 注解使用

- 使用 Spring 框架提供的注解进行依赖注入和配置
- 使用 `@Resource` 进行依赖注入（项目现有风格）
- 使用 `@Valid` 或 `@Validated` 进行参数校验
- 使用 MyBatis-Plus 提供的注解进行数据库映射
- 示例：

```java
@Resource
private EmployeeService employeeService;

@TableId(type = IdType.AUTO)
private Long employeeId;

@Schema(description = "姓名")
@NotNull(message = "姓名不能为空")
@Length(max = 30, message = "姓名最多30字符")
private String actualName;
```

## 5. 注释规范

### 5.1 类注释

- 每个类都应有类注释，使用 Javadoc 格式
- 类注释应包含：类的功能描述、作者、创建日期等信息
- 示例：

```java
/**
 * 员工 service
 *
 * @Author 木子软件: 李彦军
 * @Date 2026-01-07 21:52:46
 * @Wechat liiayy
 * @Email 773582348@qq.com
 * @Copyright <a href="https://code.muziseo.cn">木子软件</a>
 */
@Service
public class EmployeeService {
    // 类实现
}
```

### 5.2 方法注释

- 公共方法应有方法注释，使用 Javadoc 格式
- 方法注释应包含：方法功能描述、参数说明、返回值说明等
- 示例：

```java
/**
 * 查询员工列表
 * @param employeeQueryForm 查询条件
 * @return 员工分页列表
 */
public ResponseDTO<PageResult<EmployeeVO>> queryEmployee(EmployeeQueryForm employeeQueryForm) {
    // 方法实现
}
```

### 5.3 字段注释

- 实体类和表单类的字段应有注释说明
- 使用 `@Schema` 注解为 API 参数添加描述
- 示例：

```java
/**
 * 员工名称
 */
private String actualName;

@Schema(description = "姓名")
@NotNull(message = "姓名不能为空")
private String actualName;
```

### 5.4 行内注释

- 复杂逻辑或关键算法应有行内注释
- 行内注释使用 `//` 开头
- 行内注释应简洁明了，不要过于冗长
- 示例：

```java
// 查询员工角色
List<Long> employeeIdList = employeeList.stream().map(EmployeeVO::getEmployeeId).collect(Collectors.toList());
```

## 6. 文件组织

### 6.1 目录结构

- 按照功能模块划分目录
- 每个模块内部按照层次结构组织文件
- 遵循 MVC 架构的目录划分
- 示例：

```
module/
  ├── system/
  │   ├── employee/
  │   │   ├── controller/    # 控制器
  │   │   ├── service/       # 服务层
  │   │   ├── dao/           # 数据访问层
  │   │   ├── domain/        # 领域模型
  │   │   │   ├── entity/    # 实体类
  │   │   │   ├── form/      # 表单类
  │   │   │   └── vo/        # 视图对象
  │   │   └── manager/       # 业务管理器
  │   ├── department/
  │   └── role/
  └── business/
```

### 6.2 导入导出规则

- 使用静态导入时要避免命名冲突
- 导入语句应按包名排序，相同包的导入放在一起
- 避免使用通配符导入（*）
- 使用 IDE 的优化导入功能整理导入语句
- 示例：

```java
import java.util.List;
import java.util.stream.Collectors;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;

import net.lab1024.sa.admin.constant.AdminSwaggerTagConst;
import net.lab1024.sa.admin.module.system.employee.domain.form.EmployeeAddForm;
```

### 6.3 模块划分原则

- 按业务功能垂直划分模块
- 模块之间通过接口进行交互，避免直接依赖
- 通用功能抽取为独立模块
- 遵循单一职责原则

## 7. 错误处理

### 7.1 异常捕获

- 使用 try-catch 捕获可预见的异常
- 记录异常日志，包含异常信息和上下文
- 向上层返回友好的错误提示
- 避免在循环中进行异常捕获

### 7.2 错误提示

- 使用 `ResponseDTO` 统一封装错误响应
- 错误消息应清晰、具体，便于定位问题
- 对外接口的错误消息不应暴露系统内部细节
- 使用错误码标识不同类型的错误

### 7.3 边界条件处理

- 对输入参数进行验证，防止空指针异常
- 处理集合为空的情况
- 检查数组下标是否越界
- 处理数值计算可能导致的溢出
- 示例：

```java
if (CollectionUtils.isEmpty(employeeList)) {
    PageResult<EmployeeVO> pageResult = SmartPageUtil.convert2PageResult(pageParam, employeeList);
    return ResponseDTO.ok(pageResult);
}
```

## 8. 性能考量

### 8.1 数据库操作优化

- 使用分页查询，避免一次性查询大量数据
- 合理使用索引
- 避免在循环中执行数据库操作
- 使用批量操作替代单条操作
- 示例：

```java
// 分页查询
Page pageParam = SmartPageUtil.convert2PageQuery(employeeQueryForm);
List<EmployeeVO> employeeList = employeeDao.queryEmployee(pageParam, employeeQueryForm, departmentIdList);

// 批量查询替代循环查询
List<Long> employeeIdList = employeeList.stream().map(EmployeeVO::getEmployeeId).collect(Collectors.toList());
List<RoleEmployeeVO> roleEmployeeEntityList = employeeIdList.isEmpty() ? 
    Collections.emptyList() : roleEmployeeDao.selectRoleByEmployeeIdList(employeeIdList);
```

### 8.2 内存使用优化

- 避免创建不必要的对象
- 合理使用缓存
- 及时释放不再使用的资源
- 使用合适的数据结构

### 8.3 并发处理

- 避免在多线程环境下使用非线程安全的集合
- 合理使用锁机制，避免死锁
- 考虑使用线程池管理线程资源

## 9. 安全规范

### 9.1 输入验证

- 对所有用户输入进行验证
- 使用 `@Valid`、`@Validated` 和 Bean Validation 注解进行参数校验
- 验证输入的长度、格式、范围等
- 示例：

```java
@Schema(description = "手机号")
@NotNull(message = "手机号不能为空")
@Pattern(regexp = SmartVerificationUtil.PHONE_REGEXP, message = "手机号格式不正确")
private String phone;
```

### 9.2 密码安全

- 密码必须加密存储，使用加盐哈希算法
- 避免在日志中记录敏感信息
- 使用安全的随机数生成器生成密码
- 示例：

```java
// 使用安全密码服务进行密码处理
@Resource
private SecurityPasswordService securityPasswordService;
```

### 9.3 权限控制

- 使用 `@SaCheckPermission` 注解进行权限控制
- 遵循最小权限原则
- 验证用户身份和权限
- 示例：

```java
@SaCheckPermission("system:employee:add")
public ResponseDTO<String> addEmployee(@Valid @RequestBody EmployeeAddForm employeeAddForm) {
    // 方法实现
}
```

### 9.4 SQL 注入防范

- 使用参数化查询，避免字符串拼接SQL
- 使用 MyBatis-Plus 的条件构造器
- 对用户输入进行过滤和转义

### 9.5 API 安全

- 使用 `@ApiDecrypt` 注解对敏感接口进行加密处理
- 实现 API 限流和防刷机制
- 记录 API 访问日志，便于审计和排查问题
- 示例：

```java
@ApiDecrypt
public ResponseDTO<String> updatePassword(@Valid @RequestBody EmployeeUpdatePasswordForm updatePasswordForm) {
    // 方法实现
}
```

## 10. 最佳实践

### 10.1 代码复用

- 抽取通用代码为工具类或公共方法
- 使用设计模式提高代码复用性和可维护性
- 避免代码重复

### 10.2 代码简化

- 使用 Java 8+ 的新特性（Lambda表达式、Stream API、Optional等）简化代码
- 使用 Lombok 减少样板代码
- 避免过长的方法和过大的类

### 10.3 日志记录

- 合理使用日志级别（DEBUG、INFO、WARN、ERROR）
- 记录关键操作和异常信息
- 日志内容应包含足够的上下文信息
- 避免在日志中记录敏感信息

### 10.4 文档维护

- 及时更新文档，与代码保持同步
- 为关键API、配置项添加文档说明
- 使用 Swagger/OpenAPI 自动生成 API 文档

## 11. 实施指南

1. 所有新代码必须遵循本规范
2. 现有代码在修改时应逐步向本规范靠拢
3. 使用 IDE 的代码格式化和检查工具确保代码符合规范
4. 定期进行代码审查，确保规范的执行
5. 根据项目发展和团队反馈，适时更新本规范

## 12. 总结

本代码规范旨在提高 StartAdmin 项目的代码质量和可维护性。团队成员应严格遵守本规范，共同打造高质量的软件产品。
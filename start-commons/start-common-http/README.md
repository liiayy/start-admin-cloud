# start-common-http HTTP 客户端模块

## 介绍
`start-common-http` 是基于 **Forest** 框架封装的声明式 HTTP 客户端模块，提供简洁高效的第三方 API 调用能力。通过注解驱动的方式定义 HTTP 接口，让开发者专注于业务逻辑。

## 核心特性
*   **声明式 API** - 通过注解定义 HTTP 接口，无需手动编写繁琐的请求代码。
*   **核心自动配置** - 预配置了 Jackson 转换器，增强了对第三方 API 响应格式（如单值数组、空数组等）的容错性。
*   **环境隔离** - 支持按环境（dev/test/prod）加载 `http-client-{env}.yml` 配置文件，实现 API Key 等敏感信息的差异化配置。
*   **拦截器高度集成** - 通过拦截器统一处理认证（如 Bearer Token、API Key）、日志记录和异常处理。
*   **开箱即用** - 内置高德地图示例客户端。

## 快速开始

### 1. 添加依赖
```xml
<dependency>
    <groupId>cn.muziseo</groupId>
    <artifactId>start-common-http</artifactId>
</dependency>
```

### 2. 配置文件
在高德地图等服务的配置文件 `http-client-dev.yml` 中：
```yaml
forest:
  backend: okhttp3
  log-enabled: true

gaode:
  map:
    enabled: true
    api-key: ${GAODE_MAP_API_KEY:your_key}
```

### 3. 开发建议
#### 定义客户端接口
```java
@BaseRequest(
    baseURL = "https://restapi.amap.com",
    interceptor = GaodeMapInterceptor.class
)
public interface MyApiClient {
    @Get(url = "/v3/example")
    ForestResponse<String> doSomething(@Query("param") String param);
}
```

#### 实现认证拦截器
```java
public class MyInterceptor implements Interceptor<Object> {
    @Override
    public boolean beforeExecute(ForestRequest request) {
        MyProperties props = SpringUtils.getBean(MyProperties.class);
        request.addHeader("Authorization", "Bearer " + props.getToken());
        return true;
    }
}
```

## 目录结构
```text
start-common-http
├── src/main/java/cn/muziseo/common/http
│   ├── config/             # 自动配置 (Jackson 容错、环境配置加载)
│   └── client/             # 第三方客户端定义
│       └── gaode/          # 高德地图实现示例 (Properties, Interceptor, Client)
└── src/main/resources/
    ├── META-INF/           # 自动装配注册
    └── http-client-dev.yml # HTTP 模块私有配置文件
```

## 最佳实践
1.  **敏感信息**：不要在配置文件中硬编码 API Key，建议使用 `${VAR:default}` 占位符并通过环境变量或 Nacos 注入。
2.  **异常处理**：建议使用 `ForestResponse<T>` 接收返回结果，通过 `isSuccess()` 判断状态码，通过 `getResult()` 获取自动转换后的对象。
3.  **连接池**：高并发场景请在 `forest` 配置下调大 `max-connections`。

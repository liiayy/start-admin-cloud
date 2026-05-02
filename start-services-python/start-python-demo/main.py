import asyncio
from fastapi import FastAPI, Header

# 导入 Nacos 3.x/v2 SDK 组件
from v2.nacos import NacosNamingService, ClientConfigBuilder, RegisterInstanceParam

# ================= 配置区域 =================
# Nacos 服务器地址 (与 Java 服务保持一致)
NACOS_SERVER_ADDRESS = "192.168.100.20:8848"
NACOS_NAMESPACE_ID = ""  # public 命名空间的 ID 通常为空字符串
SERVICE_NAME = "start-python-demo"
SERVICE_IP = "127.0.0.1"  # 宿主机或容器真实IP
SERVICE_PORT = 8000
# ==========================================

app = FastAPI(
    title="Start Admin Python Service",
    description="Python FastAPI 异构微服务示例",
    version="1.0.0"
)

# 全局持有 client 防止被回收
naming_client = None

@app.on_event("startup")
async def startup_event():
    """在 FastAPI 启动时异步注册到 Nacos，底层自带基于 gRPC 的心跳与长连接机制"""
    global naming_client
    try:
        # 1. 构造客户端配置
        client_config = (ClientConfigBuilder()
                         .server_address(NACOS_SERVER_ADDRESS)
                         .namespace_id(NACOS_NAMESPACE_ID)
                         .build())
        
        # 2. 异步创建 Naming 客户端
        naming_client = await NacosNamingService.create_naming_service(client_config)
        
        # 3. 异步注册实例
        await naming_client.register_instance(
            request=RegisterInstanceParam(
                service_name=SERVICE_NAME, 
                group_name='DEFAULT_GROUP', 
                ip=SERVICE_IP,
                port=SERVICE_PORT, 
                weight=1.0, 
                enabled=True,
                healthy=True, 
                ephemeral=True
            )
        )
        print(f"✅ 成功注册到 Nacos: {SERVICE_NAME} ({SERVICE_IP}:{SERVICE_PORT})")
    except Exception as e:
        print(f"❌ Nacos 注册失败: {e}")

@app.on_event("shutdown")
async def shutdown_event():
    """优雅停机时清理 Nacos 连接"""
    global naming_client
    if naming_client:
        try:
            await naming_client.shutdown()
            print("✅ 已断开 Nacos 连接")
        except Exception as e:
            print(f"⚠️ 断开 Nacos 连接时发生异常: {e}")

@app.get("/admin/python/test")
async def test_endpoint(x_user_id: str = Header(None, alias="X-User-Id")):
    """
    测试接口。网关会透传 X-User-Id 头。
    """
    if not x_user_id:
        return {"code": 401, "msg": "未获取到网关透传的用户身份，请从网关入口访问"}
    
    return {
        "code": 200, 
        "msg": "调用成功！Python 微服务为您服务", 
        "data": {
            "service_name": SERVICE_NAME,
            "current_user_id": x_user_id
        }
    }

if __name__ == "__main__":
    import uvicorn
    # 启动服务器
    uvicorn.run("main:app", host="0.0.0.0", port=SERVICE_PORT, reload=True)

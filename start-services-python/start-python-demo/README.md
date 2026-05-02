# Start Admin Python Demo Service

这是一个用于演示如何在基于 Spring Cloud (Java) 的微服务架构中，优雅地接入异构语言 (Python FastAPI) 的示例项目。

## 特性
- 采用 **FastAPI** 提供极速 API。
- 采用 **uv** 替代传统 pip 管理依赖与虚拟环境。
- 采用 **Ruff** 提供超快速的代码检查与格式化。
- 使用 `nacos-sdk-python` 自动注册到 Nacos 注册中心。
- 支持接收 Spring Cloud Gateway 透传的鉴权 Header。

## 开发环境搭建

1. **安装 uv 工具**
   如果您还没有安装 uv，请先安装：
   ```bash
   # Windows
   winget install --id=astral-sh.uv  -e
   # 或
   pip install uv
   ```

2. **同步依赖与环境**
   在本项目根目录执行：
   ```bash
   uv sync
   ```
   uv 会自动帮您下载指定的 Python 解释器（如果没有），创建 `.venv` 虚拟环境，并安装 `pyproject.toml` 中的所有依赖。

3. **运行服务**
   ```bash
   uv run uvicorn main:app --reload
   ```
   *服务将在 `http://127.0.0.1:8000` 启动，并在后台自动向 Nacos 注册。*

## 代码格式化与检查 (Ruff)

- **格式化代码**：
  ```bash
  uvx ruff format .
  ```
- **检查并自动修复常见规范错误**：
  ```bash
  uvx ruff check . --fix
  ```

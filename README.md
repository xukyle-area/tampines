# tampines

## 项目简介

`tampines` 是一个多模块的 Java 金融行情与服务系统，包含 Redis 实时行情写入、WebSocket 客户端、Flink 处理等功能模块。

## 目录结构

- `market-common`：通用工具与常量模块
- `market-flink`：Flink 相关流式处理模块
- `market-outer`：行情采集、WebSocket、Redis 写入等外部服务模块
- `market-service`：服务层及相关配置
- `scripts`：开发辅助脚本

## 快速开始

### 1. 环境准备
- JDK 8 或以上（推荐 JDK 11+）
- Maven 3.6+
- Redis（本地或远程，需配置密码）

### 2. 构建项目
```sh
mvn clean package
```

### 3. 运行示例
以 `market-outer` 为例：
```sh
cd market-outer
mvn spring-boot:run
```
或
```sh
java -jar target/market-outer-*.jar
```

### 4. 配置说明
编辑 `market-outer/src/main/resources/application.yml`，配置 Redis 地址和密码：
```yaml
app:
  redis:
    cluster:
      nodes: localhost:6379
      password: "your_redis_password"
```

## 常见问题
- **Redis 连接异常**：请确认 Redis 服务已启动、密码正确，且防火墙未阻断端口。
- **WebSocket SSL 握手失败**：请确认 WebSocket 地址和协议（wss/ws）正确，JDK 版本支持 TLS 1.2+。

## 贡献
欢迎提交 issue 和 PR。

## License
MIT

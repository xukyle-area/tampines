# Tampines Market Data Processing System

## 概述

Tampines 是一个基于 Apache Flink 的实时市场数据处理系统，专门用于处理金融市场的订单簿、交易数据和行情信息。该系统采用流处理架构，能够实时聚合和计算市场数据，提供高性能、低延迟的数据处理能力。

### 主要特性
- **实时订单簿维护**：基于 Flink State 维护买卖双方的订单簿状态
- **多数据源支持**：支持 Kafka、Redis 等数据源的集成
- **高性能流处理**：利用 Flink 的分布式计算能力处理海量市场数据
- **模块化架构**：分为公共模块、Flink 处理模块和外部接口模块

### 系统架构
- `market-common`: 公共模块，包含数据模型、工具类和常量定义
- `market-flink`: Flink 作业模块，实现核心的流处理逻辑
- `market-outer`: 外部接口模块，提供数据接入和输出功能

## 环境准备

### 依赖组件
- **Zookeeper**: localhost:2181
- **Kafka**: localhost:9092
- **Redis**: localhost:6379
- **Flink**: 1.17.2

### 安装步骤

#### 1. 安装 Zookeeper 和 Kafka
1. 下载 Kafka（包含 Zookeeper）：
   ```bash
   curl -O https://downloads.apache.org/kafka/3.5.1/kafka_2.13-3.5.1.tgz
   ```
2. 解压并移动到 `/usr/local/kafka`：
   ```bash
   tar -xzf kafka_2.13-3.5.1.tgz
   sudo mv kafka_2.13-3.5.1 /usr/local/kafka
   ```
3. 配置环境变量：
   ```bash
   echo 'export KAFKA_HOME=/usr/local/kafka' >> ~/.zshrc
   echo 'export PATH=$PATH:$KAFKA_HOME/bin' >> ~/.zshrc
   source ~/.zshrc
   ```

#### 2. 安装 Redis
1. 使用 Homebrew 安装 Redis：
   ```bash
   brew install redis
   ```
2. 配置 Redis（可选）：
   ```bash
   cp /opt/homebrew/etc/redis.conf ~/redis.conf
   vim ~/redis.conf
   ```
3. 启动 Redis：
   ```bash
   redis-server --daemonize yes
   ```

#### 3. 安装 Flink
1. 下载 Flink：
   ```bash
   curl -O https://archive.apache.org/dist/flink/flink-1.17.2/flink-1.17.2-bin-scala_2.12.tgz
   ```
2. 解压并移动到 `/usr/local/flink`：
   ```bash
   tar -xzf flink-1.17.2-bin-scala_2.12.tgz
   sudo mv flink-1.17.2 /usr/local/flink
   ```
3. 配置环境变量：
   ```bash
   echo 'export FLINK_HOME=/usr/local/flink' >> ~/.zshrc
   echo 'export PATH=$PATH:$FLINK_HOME/bin' >> ~/.zshrc
   source ~/.zshrc
   ```
4. 验证安装：
   ```bash
   flink --version
   ```
5. 端口
Flink 默认会使用两个主要的端口：
   1. **REST 端口**：用于与 Flink 的 REST API 通信，默认端口是 **8081**。这个端口通常用于访问 Flink 的 Web UI 和提交作业。
   2. **RPC 端口**：用于 Flink 内部组件之间的通信（如 JobManager 和 TaskManager），默认端口是 **6123**。

如果需要修改这些端口，可以在 Flink 的配置文件 `flink-conf.yaml` 中进行设置：

- 修改 REST 端口：
  ```yaml
  rest.port: 8081
  ```

- 修改 RPC 端口：
  ```yaml
  jobmanager.rpc.port: 6123
  ```


### 验证安装
- **Zookeeper**: 检查端口监听
  ```bash
  lsof -i :2181
  ```
- **Kafka**: 检查端口监听
  ```bash
  lsof -i :9092
  ```
- **Redis**: 测试连接
  ```bash
  redis-cli ping
  # 应返回: PONG
  ```
- **Flink**: 检查版本
  ```bash
  flink --version
  ```

## 🎯 Kafka & ZooKeeper

### 启动服务

```bash
# 1. 启动 ZooKeeper（后台运行）
zookeeper-server-start.sh /usr/local/kafka/config/zookeeper.properties > /tmp/zookeeper.log 2>&1 &

# 2. 等待 ZooKeeper 启动
sleep 5

# 3. 启动 Kafka（后台运行）
kafka-server-start.sh /usr/local/kafka/config/server.properties > /tmp/kafka.log 2>&1 &

# 4. 等待 Kafka 启动
sleep 5
```

### 查看状态

```bash
# 查看 Java 进程
jps | grep -E "Kafka|QuorumPeerMain"

# 查看端口监听
lsof -i :2181  # ZooKeeper
lsof -i :9092  # Kafka

# 查看日志
tail -f /tmp/zookeeper.log
tail -f /tmp/kafka.log
```

### Topic 管理

```bash
# 创建 Topic
kafka-topics.sh --create \
  --topic api \
  --bootstrap-server localhost:9092 \
  --partitions 3 \
  --replication-factor 1

# 列出所有 Topic
kafka-topics.sh --list --bootstrap-server localhost:9092

# 查看 Topic 详情
kafka-topics.sh --describe \
  --topic market-data \
  --bootstrap-server localhost:9092

# 删除 Topic
kafka-topics.sh --delete \
  --topic market-data \
  --bootstrap-server localhost:9092
```

### 停止服务

```bash
# 1. 先停止 Kafka
kafka-server-stop.sh

# 2. 等待几秒
sleep 3

# 3. 再停止 ZooKeeper
zookeeper-server-stop.sh

# 4. 验证已停止
jps | grep -E "Kafka|QuorumPeerMain"
```

---

## 🔴 Redis

### 启动服务

```bash
# 后台启动（推荐）
redis-server --daemonize yes

# 或使用配置文件后台启动
redis-server /opt/homebrew/etc/redis.conf --daemonize yes
```

### 查看状态

```bash
# 检查进程
ps aux | grep redis-server

# 检查端口
lsof -i :6379

# 连接测试
redis-cli ping
# 应返回: PONG
```

### 停止服务

```bash
# 优雅关闭
redis-cli shutdown

# 或强制停止
pkill redis-server
```

---

## 🌊 Flink

### 启动集群

```bash
# 启动 Flink 集群
start-cluster.sh

# 查看 Java 进程
jps | grep -E "StandaloneSession|TaskManager"

# 访问 Web UI
open http://localhost:8081
# 或直接在浏览器打开: http://localhost:8081
```

### 查看日志

```bash
# 查看 JobManager 日志
tail -f $FLINK_HOME/log/flink-*-standalonesession-*.log

# 查看 TaskManager 日志
tail -f $FLINK_HOME/log/flink-*-taskexecutor-*.log

# 查看所有日志
tail -f $FLINK_HOME/log/flink-*.log
```

### 作业管理

```bash
# 提交作业（指定主类）
flink run -c com.ganten.market.flink.job.TickJob target/market-flink-1.0.0-SNAPSHOT.jar

# 列出运行中的作业
flink list -r

# 列出所有作业（包括已完成）
flink list -a

# 查看作业详情
flink info <job-id>

# 取消作业
flink cancel <job-id>

```

### 停止集群

```bash
# 停止 Flink 集群
stop-cluster.sh

# 验证已停止
jps | grep -E "StandaloneSession|TaskManager"
```

### 测试数据

项目提供了示例订单数据用于测试 OrderBookProcessor：

#### 示例订单数据
`sample-orders.json` 包含了完整的订单操作序列，模拟了订单簿的各种操作：

1. **初始订单簿建立**：添加买单和卖单
2. **订单更新**：增加现有价格的订单数量
3. **部分成交**：减少订单数量
4. **完全成交**：删除整个价格档位
5. **新增订单**：添加新的价格档位

#### 使用示例数据
可以通过以下方式将数据发送到 Kafka：

```bash
# 创建订单主题
kafka-topics.sh --create --topic order --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1

# 发送示例订单数据到 Kafka
cat sample-orders.json | kafka-console-producer.sh --topic order --bootstrap-server localhost:9092
```

#### 订单字段说明
- `contractId`: 合约ID，用于按合约分组处理
- `timestamp`: 时间戳（毫秒）
- `price`: 订单价格
- `quantity`: 订单数量
- `amount`: 订单金额（price × quantity）
- `side`: 买卖方向（"BID"=买单，"ASK"=卖单）
- `action`: 操作类型（"INSERT"=新增/增加，"DELETE"=减少/删除）
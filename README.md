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
  --topic market-data \
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
flink run -c com.ganten.market.flink.TickJob \
  target/market-flink-1.0.0-SNAPSHOT.jar

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
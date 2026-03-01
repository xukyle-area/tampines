# Market Flink Jobs 文档

本文档详细介绍 `market-flink` 模块中的三个 Flink 流处理作业：**TickerJob**、**CandleJob** 和 **OrderbookJob**。这三个作业共同构成了市场数据实时计算引擎的核心，负责将原始交易数据和订单数据转换为对外展示的 Ticker 行情、K线蜡烛图和订单簿深度数据。

---

## 整体架构

```
         ┌────────────────┐
         │  Kafka Source  │
         └───────┬────────┘
                 │
    ┌────────────┼────────────┐
    │            │            │
    ▼            ▼            ▼
┌────────┐  ┌────────┐  ┌────────────┐
│ Ticker │  │ Candle │  │ Orderbook  │
│  Job   │  │  Job   │  │    Job     │
└───┬────┘  └───┬────┘  └─────┬──────┘
    │           │             │
    └───────────┴──────┬──────┘
                       ▼
              ┌─────────────────┐
              │ CompositeWriter │
              ├─────────────────┤
              │  ● RedisWriter  │  → 持久化存储
              │  ● KafkaWriter  │  → MQTT 推送
              └─────────────────┘
```

所有 Job 都采用 **Kafka 作为数据源**，处理完成后通过 **CompositeWriter** 同时写入 **Redis（存储）** 和 **Kafka（用于 MQTT 消息推送到客户端）**。

---

## 1. TickerJob（行情 Ticker 计算）

### 1.1 功能概述

TickerJob 负责计算每个合约（Contract）的 **24小时滚动行情快照**。它持续聚合过去24小时内的交易数据，并每秒输出一次最新的行情统计。

### 1.2 数据输入

- **数据源**：Kafka `trade` topic
- **数据类型**：`Trade` 对象
  - `id`：交易唯一标识
  - `price`：成交价格
  - `volume`：成交量
  - `time`：成交时间戳
  - `isBuyerMaker`：是否为买方挂单成交

### 1.3 处理流程

#### 第一步：按合约分组（KeyBy）
数据流首先按 `contractId`（合约ID）进行分组。这确保同一合约的所有交易数据会被路由到同一个处理实例，从而能够正确计算该合约的统计指标。

#### 第二步：滑动时间窗口
采用 **24小时滑动窗口**，滑动步长为 **1秒**。这意味着：
- 窗口大小：`24 hours`
- 滑动间隔：`1 second`
- 每秒计算一次过去24小时的累积数据

#### 第三步：聚合计算（TickerAggregator）
在窗口内对交易数据进行增量聚合，维护以下中间状态：

| 字段 | 说明 |
|------|------|
| `firstPrice` | 窗口内第一笔成交价（开盘价基准） |
| `lastPrice` | 窗口内最后一笔成交价（最新价） |
| `highest` | 窗口内最高成交价 |
| `lowest` | 窗口内最低成交价 |
| `volume` | 窗口内累计成交量 |

聚合逻辑支持分布式场景下的累加器合并（`merge`），确保在并行处理时数据的正确性。

#### 第四步：结果处理（TickerProcessor）
将聚合结果转换为最终的 `Ticker` 对象：
- 计算 **价格变化值**：`change = lastPrice - firstPrice`
- 计算 **涨跌幅百分比**：`changePercent = (change / firstPrice) × 100%`
- 保留精度：涨跌幅保留2位小数

### 1.4 数据输出

输出 `Ticker` 对象包含：

| 字段 | 说明 |
|------|------|
| `contractId` | 合约ID |
| `last` | 最新成交价 |
| `highest` | 24h最高价 |
| `lowest` | 24h最低价 |
| `volume` | 24h成交量 |
| `change` | 价格变化值 |
| `changePercent` | 涨跌幅百分比 |

### 1.5 存储与推送

- **Redis**：以 Hash 结构存储，key 格式为 `ticker:{market}:{contract}`
- **Kafka/MQTT**：推送到 topic `mqtt/quote/{symbol}/ticker`，供客户端实时订阅

---

## 2. CandleJob（K线蜡烛图计算）

### 2.1 功能概述

CandleJob 负责生成 **K线蜡烛图数据**，支持多种时间周期。当前配置支持：
- **1分钟 K线**（60秒）
- **5分钟 K线**（300秒）

### 2.2 数据输入

- **数据源**：Kafka `trade` topic
- **数据类型**：`Trade` 对象（与 TickerJob 共享同一数据源）

### 2.3 处理流程

#### 第一步：按合约分组
与 TickerJob 相同，按 `contractId` 分组确保同一合约的交易数据聚合到一起。

#### 第二步：滚动时间窗口（Tumbling Window）
采用 **滚动窗口**（非滑动），窗口大小等于 K线周期：
- 1分钟K线：窗口大小 60秒
- 5分钟K线：窗口大小 300秒

滚动窗口的特点是窗口之间不重叠，每个交易只会落入一个窗口，这正好符合 K线的语义——每根蜡烛代表一个固定时间段。

#### 第三步：蜡烛图计算（CandleProcessor）
在每个窗口结束时，遍历窗口内的所有交易，计算：

| 字段 | 计算逻辑 |
|------|----------|
| `open` | 窗口内第一笔成交价 |
| `close` | 窗口内最后一笔成交价 |
| `high` | 窗口内最高成交价 |
| `low` | 窗口内最低成交价 |
| `volume` | 窗口内累计成交量 |
| `startTime` | 窗口起始时间戳 |

**去重机制**：使用 `HashSet` 记录已处理的 `tradeId`，防止重复计算同一笔交易（例如 Kafka 消息重复投递场景）。

#### 第四步：多周期并行计算
Job 启动时为每个配置的周期（60秒、300秒）创建独立的处理流：
- 共享同一个 KeyedStream 数据源
- 使用 **Slot Sharing Group** 优化资源利用
- 各周期独立计算，互不干扰

### 2.4 数据输出

输出 `Candle` 对象包含：

| 字段 | 说明 |
|------|------|
| `startTime` | 蜡烛图起始时间（毫秒时间戳） |
| `open` | 开盘价 |
| `close` | 收盘价 |
| `high` | 最高价 |
| `low` | 最低价 |
| `volume` | 成交量 |

### 2.5 存储与推送

- **Redis**：使用 Sorted Set 存储，`score` 为蜡烛图起始时间，key 格式为 `candle:{market}:{contract}:{resolution}`
  - 自动清理过期数据（默认保留最近 1502 根）
- **Kafka/MQTT**：推送到 topic `mqtt/quote/{symbol}/candle/?resolution={resolution}`

---

## 3. OrderbookJob（订单簿深度计算）

### 3.1 功能概述

OrderbookJob 负责维护实时 **订单簿（Order Book）**，展示市场买卖方的挂单深度。支持多种价格精度分组：
- **1倍 tick size**：最高精度
- **5倍 tick size**：聚合显示

### 3.2 数据输入

- **数据源**：Kafka `order` topic
- **数据类型**：`Order` 对象
  - `price`：挂单价格
  - `quantity`：挂单数量
  - `amount`：挂单金额
  - `side`：买卖方向（BID/ASK）
  - `action`：操作类型（INSERT/DELETE）

### 3.3 处理流程

#### 第一步：按合约分组
按 `contractId` 分组，确保同一合约的所有订单事件由同一处理实例处理。

#### 第二步：状态管理（MapState）
使用 Flink 的 **MapState** 维护订单簿状态：
- `bidState`：买单状态，key 为分组后价格，value 为该价位累计数量
- `askState`：卖单状态，结构同上

#### 第三步：价格分组
根据 `resolution` 参数对价格进行分组：
- 分组粒度 = 合约 tick size × resolution
- 例如：tick size 为 0.01，resolution 为 5，则分组粒度为 0.05
- 分组算法：`groupedPrice = floor(price / grouping) × grouping`

#### 第四步：订单事件处理（OrderBookProcessor）
根据订单的 `action` 类型更新状态：

**INSERT（新增挂单）**：
1. 计算分组后价格
2. 获取该价位当前累计数量
3. 新数量 = 当前数量 + 订单数量
4. 更新状态

**DELETE（撤销/成交）**：
1. 计算分组后价格
2. 获取该价位当前累计数量
3. 新数量 = 当前数量 - 订单数量
4. 若新数量 ≤ 0，则删除该价位；否则更新状态

#### 第五步：定时输出（ProcessingTime Timer）
采用 **处理时间定时器** 每秒输出一次订单簿快照：
- 每次处理订单事件时注册下一个定时器（1秒后）
- 定时器触发时，收集当前 `bidState` 和 `askState` 的所有数据
- 构建 `OrderBook` 对象并输出

这种设计的好处是：
- 无论订单更新频率多高，输出频率固定为每秒一次
- 避免高频更新淹没下游系统
- 保证数据的实时性（最多延迟1秒）

### 3.4 数据输出

输出 `OrderBook` 对象包含：

| 字段 | 说明 |
|------|------|
| `contractId` | 合约ID |
| `market` | 市场标识 |
| `grouping` | 价格分组粒度 |
| `bids` | 买单深度（价格 → 数量映射） |
| `asks` | 卖单深度（价格 → 数量映射） |

### 3.5 存储与推送

- **Redis**：分别存储买单和卖单的 Hash
  - 买单 key：`orderbook:{market}:{contract}:BID:{grouping}`
  - 卖单 key：`orderbook:{market}:{contract}:ASK:{grouping}`
  - 每次更新先清空再写入，保证数据一致性
- **Kafka/MQTT**：推送到 topic `mqtt/quote/{symbol}/orderBook/?grouping={grouping}`

---

## 技术特点总结

| 特性 | TickerJob | CandleJob | OrderbookJob |
|------|-----------|-----------|--------------|
| **数据源** | trade | trade | order |
| **窗口类型** | 滑动窗口 | 滚动窗口 | 无窗口（状态驱动） |
| **更新频率** | 每秒 | 按周期（60s/300s） | 每秒 |
| **状态管理** | 增量聚合 | 窗口内遍历 | MapState |
| **并行度** | 1 | 1 | 1 |

### 为什么并行度设为 1？

这三个 Job 都将并行度设置为 1，原因包括：
1. **数据一致性**：避免同一合约数据被多实例处理导致状态不一致
2. **顺序保证**：确保交易和订单按时间顺序处理
3. **简化运维**：在单机场景下降低复杂度

在高吞吐场景下，可以通过增加合约分片来水平扩展。

---

## 扩展说明

### 添加新的 K线周期

在 `CandleJob` 的 `RESOLUTIONS` 数组中添加新的秒数即可：

```java
private static final int[] RESOLUTIONS = {60, 300, 900, 3600}; // 1分钟、5分钟、15分钟、1小时
```

### 添加新的订单簿精度

在 `OrderbookJob` 的 `RESOLUTIONS` 数组中添加新的倍数：

```java
private static final int[] RESOLUTIONS = {1, 5, 10, 100}; // 1x, 5x, 10x, 100x tick size
```

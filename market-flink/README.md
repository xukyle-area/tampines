# Flink 常见 Aggregate 与 Process 算子总结

## 一、整体概览

| 类型                                          | 所属类别               | 输入/输出             | 典型用途                  | 是否有状态 | 是否需要窗口 |
| ------------------------------------------- | ------------------ | ----------------- | --------------------- | ----- | ------ |
| `ReduceFunction`                            | Aggregation        | 同类型 -> 同类型        | 累加聚合（如 sum, min, max） | 是     | 一般配合窗口 |
| `AggregateFunction`                         | Aggregation        | 泛型 -> 泛型          | 自定义聚合逻辑（复杂统计）         | 是     | 一般配合窗口 |
| `ProcessFunction`                           | Process            | 任意 -> 任意          | 任意事件处理逻辑              | 可选    | 否      |
| `KeyedProcessFunction`                      | Process            | KeyedStream -> 任意 | 按 key 的状态/定时器处理       | 是     | 否      |
| `ProcessWindowFunction`                     | Process + Window   | 一组元素 -> 任意输出      | 访问窗口元数据、全量计算          | 是     | 是      |
| `AggregateFunction + ProcessWindowFunction` | Aggregate + Window | 增量 + 全量           | 高效聚合 + 元数据输出          | 是     | 是      |
| `CoProcessFunction`                         | Process            | 两条流 -> 任意         | 双流匹配或联动               | 是     | 否      |
| `ProcessAllWindowFunction`                  | Window All         | 全局窗口处理            | 无 key 的全量计算           | 是     | 是      |

---

## 二、Aggregation 算子

### 1. `ReduceFunction<T>`

**用途**：简单的两两聚合（如求和、求最大值）。
**优点**：性能高（增量计算）；
**缺点**：不支持访问窗口元信息。

```java
DataStream<Integer> result = stream
    .keyBy(v -> v.f0)
    .reduce((v1, v2) -> v1 + v2);
```

**典型用途**：

* 求累计成交量、最高价、最低价；
* 实时滑动窗口增量聚合。

---

### 2. `AggregateFunction<IN, ACC, OUT>`

**用途**：更通用的增量聚合，可以定义中间累加器。
**优点**：性能高、内存占用小；
**缺点**：无法直接访问窗口上下文（需与 `ProcessWindowFunction` 组合）。

```java
public class TickAggregator implements AggregateFunction<TradeInfo, TickAccumulator, Tick> {
    public TickAccumulator createAccumulator() { return new TickAccumulator(); }
    public TickAccumulator add(TradeInfo t, TickAccumulator acc) { ...; return acc; }
    public Tick getResult(TickAccumulator acc) { return acc.toTick(); }
    public TickAccumulator merge(TickAccumulator a, TickAccumulator b) { ... }
}
```

---

### 3. `AggregateFunction + ProcessWindowFunction`

**用途**：既想增量聚合提高性能，又想在输出时访问窗口信息（如 start/end time）。
**用法**：

```java
stream
  .keyBy(TradeInfo::getContract)
  .window(SlidingEventTimeWindows.of(Time.hours(24), Time.minutes(1)))
  .aggregate(new TickAggregator(), new TickWindowFunction());
```

**TickWindowFunction** 示例：

```java
public class TickWindowFunction
    extends ProcessWindowFunction<Tick, Tick, Contract, TimeWindow> {
    public void process(Contract key, Context ctx, Iterable<Tick> agg, Collector<Tick> out) {
        Tick tick = agg.iterator().next();
        tick.setWindowEnd(ctx.window().getEnd());
        out.collect(tick);
    }
}
```

---

## 三、Process 类算子

### 1. `ProcessFunction<IN, OUT>`

**用途**：最通用的底层处理函数，可以访问：

* 时间戳（`ctx.timestamp()`）
* 当前 watermark（`ctx.timerService().currentWatermark()`）
* 注册定时器（`ctx.timerService().registerProcessingTimeTimer()`）

```java
public class AlertProcess extends ProcessFunction<Event, Alert> {
    public void processElement(Event e, Context ctx, Collector<Alert> out) {
        if (e.isAbnormal()) {
            out.collect(new Alert(e.getId(), e.getValue()));
        }
    }
}
```

**适合场景**：

* 单流规则过滤；
* 异步报警；
* 指标清洗、拆分。

---

### 2. `KeyedProcessFunction<K, IN, OUT>`

**用途**：在按 Key 分组的流上进行复杂的事件驱动逻辑。
可以使用：

* Keyed State（ValueState、ListState、MapState 等）
* 定时器（Processing / Event Time）

```java
public class OrderTimeout extends KeyedProcessFunction<String, OrderEvent, Alert> {
    private ValueState<Long> timerState;

    @Override
    public void open(Configuration conf) {
        timerState = getRuntimeContext().getState(new ValueStateDescriptor<>("timer", Long.class));
    }

    @Override
    public void processElement(OrderEvent event, Context ctx, Collector<Alert> out) throws Exception {
        long timer = ctx.timerService().currentProcessingTime() + 10_000;
        ctx.timerService().registerProcessingTimeTimer(timer);
        timerState.update(timer);
    }

    @Override
    public void onTimer(long ts, OnTimerContext ctx, Collector<Alert> out) {
        out.collect(new Alert(ctx.getCurrentKey(), "order timeout"));
    }
}
```

**典型用途**：

* 延迟检测；
* 订单超时；
* 状态流控；
* 自定义 join。

---

### 3. `ProcessWindowFunction<IN, OUT, KEY, W>`

**用途**：对窗口内的所有元素进行全量计算，可访问窗口上下文（start/end time 等）。
**特点**：会保留窗口中所有元素到内存。

```java
public class TickWindowFunction
    extends ProcessWindowFunction<TradeInfo, Tick, Contract, TimeWindow> {

    public void process(Contract key, Context ctx, Iterable<TradeInfo> elements, Collector<Tick> out) {
        BigDecimal high = BigDecimal.ZERO;
        BigDecimal low = BigDecimal.valueOf(Double.MAX_VALUE);
        for (TradeInfo t : elements) {
            high = high.max(t.getPrice());
            low = low.min(t.getPrice());
        }
        Tick tick = new Tick();
        tick.setHighest(high);
        tick.setLowest(low);
        tick.setContract(key);
        out.collect(tick);
    }
}
```

**适合场景**：

* 需要窗口时间（如 24h tick、K线）；
* 需要一次性输出窗口聚合结果；
* 小窗口、有限内存场景。

---

### 4. `CoProcessFunction<IN1, IN2, OUT>`

**用途**：处理两条流的联合逻辑，例如流 join、流控制。

```java
public class MatchOrders extends CoProcessFunction<OrderEvent, PaymentEvent, Matched> {
    private ValueState<OrderEvent> orderState;
    private ValueState<PaymentEvent> paymentState;

    public void processElement1(OrderEvent order, Context ctx, Collector<Matched> out) throws Exception {
        PaymentEvent payment = paymentState.value();
        if (payment != null) out.collect(new Matched(order, payment));
        else orderState.update(order);
    }

    public void processElement2(PaymentEvent payment, Context ctx, Collector<Matched> out) throws Exception {
        OrderEvent order = orderState.value();
        if (order != null) out.collect(new Matched(order, payment));
        else paymentState.update(payment);
    }
}
```

---

## 四、常用组合模式

| 场景          | 推荐组合                                        | 原因            |
| ----------- | ------------------------------------------- | ------------- |
| 实时滑动窗口统计    | `AggregateFunction + ProcessWindowFunction` | 增量聚合 + 窗口时间输出 |
| Tick/K线计算   | `AggregateFunction + ProcessWindowFunction` | 性能高、可访问窗口上下文  |
| 延迟检测 / 超时报警 | `KeyedProcessFunction`                      | 使用定时器实现       |
| 双流关联        | `CoProcessFunction`                         | 独立管理两条流状态     |
| 简单过滤/转换     | `ProcessFunction`                           | 灵活处理单流事件      |

---

## 五、总结对比图

| 类别                        | 是否有 Key | 是否窗口 | 是否全量 | 性能 | 可访问上下文 | 典型用途      |
| ------------------------- | ------- | ---- | ---- | -- | ------ | --------- |
| ReduceFunction            | 是       | 可有   | 否    | 高  | 否      | 简单聚合      |
| AggregateFunction         | 是       | 可有   | 否    | 高  | 否      | 自定义增量聚合   |
| ProcessWindowFunction     | 是       | 是    | 是    | 中  | 是      | 全量窗口计算    |
| Aggregate + ProcessWindow | 是       | 是    | 否    | 高  | 是      | Tick/K线聚合 |
| ProcessFunction           | 否       | 否    | 任意   | 高  | 是      | 单流清洗/报警   |
| KeyedProcessFunction      | 是       | 否    | 任意   | 高  | 是      | 有状态逻辑、定时器 |
| CoProcessFunction         | 否/是     | 否    | 任意   | 高  | 是      | 双流匹配      |

---

## 六、实践建议

1. **优先使用增量算子**（`Reduce` / `Aggregate`），全量算子只在确实需要窗口上下文时使用；
2. **所有带 Key 的 ProcessFunction 都可以使用 State**（Value/List/Map），注意 TTL；
3. **滑动/滚动窗口中最好配合 watermark**，防止迟到数据过早触发；
4. **ProcessFunction 是 Flink 最底层接口**，可以实现几乎所有自定义逻辑；
5. **在复杂计算中推荐 `aggregate + processWindow` 组合**：性能与灵活性兼顾。

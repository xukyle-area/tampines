#!/usr/bin/env python3
"""
实时数字订单簿显示器
从 Kafka topic 消费订单簿数据并实时显示
"""

import json
import time
from collections import defaultdict
from kafka import KafkaConsumer
import os
from rich.console import Console
from rich.table import Table
from rich.live import Live
from rich.text import Text
from rich.panel import Panel
from rich.layout import Layout
from rich.columns import Columns


class OrderBookDisplay:
    def __init__(self, bootstrap_servers='localhost:9092', topic='api', target_grouping=0.01):
        self.bootstrap_servers = bootstrap_servers
        self.topic = topic
        self.target_grouping = target_grouping  # 只显示指定分组的数据
        self.console = Console()
        self.order_books = defaultdict(dict)  # {contract: orderbook}
        self.previous_bids = {}  # 存储上一个状态的买单
        self.previous_asks = {}  # 存储上一个状态的卖单

        # 创建消费者
        self.consumer = KafkaConsumer(
            self.topic,
            bootstrap_servers=[self.bootstrap_servers],
            auto_offset_reset='latest',
            enable_auto_commit=True,
            group_id='orderbook-display',
            value_deserializer=lambda x: json.loads(x.decode('utf-8'))
        )

    def parse_orderbook_message(self, message):
        """解析订单簿消息，只处理指定分组的数据"""
        try:
            payload = json.loads(message['payload'])
            grouping = payload.get('grouping', 0.01)

            # 只处理指定分组的数据
            if grouping != self.target_grouping:
                return None

            contract_id = payload.get('contractId', 1)
            market = payload.get('market', 'UNKNOWN')

            return {
                'grouping': grouping,
                'contract_id': contract_id,
                'market': market,
                'timestamp': payload.get('timestamp', 0),
                'bids': payload.get('bids', {}),
                'asks': payload.get('asks', {})
            }
        except (json.JSONDecodeError, KeyError) as e:
            self.console.print(f"[red]解析消息失败: {e}[/red]")
            return None

    def create_orderbook_table(self, bids, asks, title, previous_bids=None, previous_asks=None):
        """创建订单簿表格 - 买单和卖单在同一表格中上下排列，变化时高亮显示"""
        from rich.table import Table
        from rich.panel import Panel
        from rich.text import Text

        if previous_bids is None:
            previous_bids = {}
        if previous_asks is None:
            previous_asks = {}

        # 创建合并的订单簿表格
        table = Table(title=title, show_header=True, header_style="bold blue")
        table.add_column("类型", style="bold white", justify="center", min_width=6)
        table.add_column("价格", style="white", justify="right", min_width=12)
        table.add_column("数量", style="white", justify="right", min_width=10)

        # 添加卖单数据（从高到低）
        ask_prices = sorted([float(p) for p in asks.keys()], reverse=True)
        for price in ask_prices:
            original_key = next(k for k in asks.keys() if float(k) == price)
            qty = asks[original_key]
            prev_qty = previous_asks.get(original_key, 0)
            
            if qty > prev_qty:
                style = "rgb(0,255,0)"
            elif qty < prev_qty:
                style = "rgb(255,0,0)"
            else:
                style = "white"
            
            table.add_row(
                Text("卖单", style="bold white"),
                Text(f"{price:.2f}", style=style),
                Text(f"{qty}", style=style)
            )

        # 添加分隔行
        table.add_row("━━━━━", "━━━━━", "━━━━━")

        # 添加买单数据（从高到低）
        bid_prices = sorted([float(p) for p in bids.keys()], reverse=True)
        for price in bid_prices:
            original_key = next(k for k in bids.keys() if float(k) == price)
            qty = bids[original_key]
            prev_qty = previous_bids.get(original_key, 0)
            
            # 确定样式：增加时浅绿色，减少时浅红色，不变时白色
            if qty > prev_qty:
                style = "rgb(0,255,0)"
            elif qty < prev_qty:
                style = "rgb(255,0,0)"
            else:
                style = "white"
            
            table.add_row(
                Text("买单", style="bold white"),
                Text(f"{price:.2f}", style=style),
                Text(f"{qty}", style=style)
            )

        return Panel(table, border_style="blue")

    def create_summary_panel(self, orderbook_data):
        """创建汇总信息面板"""
        if not orderbook_data:
            return Panel("暂无数据", title="汇总信息")

        grouping = orderbook_data['grouping']
        contract_id = orderbook_data['contract_id']
        market = orderbook_data['market']
        timestamp = time.strftime('%H:%M:%S', time.localtime(orderbook_data['timestamp'] / 1000))

        bids = orderbook_data['bids']
        asks = orderbook_data['asks']

        # 计算汇总信息
        total_bid_qty = sum(bids.values())
        total_ask_qty = sum(asks.values())
        bid_prices = [float(p) for p in bids.keys()]
        ask_prices = [float(p) for p in asks.keys()]

        best_bid = max(bid_prices) if bid_prices else 0
        best_ask = min(ask_prices) if ask_prices else 0
        spread = best_ask - best_bid if best_bid and best_ask else 0

        summary_text = f"""
合约ID: {contract_id}
市场: {market}
分组: {grouping}
时间: {timestamp}

买单总数: {total_bid_qty}
卖单总数: {total_ask_qty}
最佳买价: {best_bid:.2f}
最佳卖价: {best_ask:.2f}
价差: {spread:.2f}
        """.strip()

        return Panel(summary_text, title="📊 汇总信息", border_style="blue")

    def run(self):
        """运行实时显示"""
        self.console.print("[bold green]🚀 启动实时订单簿显示器...[/bold green]")
        self.console.print(f"📡 连接到 Kafka: {self.bootstrap_servers}")
        self.console.print(f"📋 监听 Topic: {self.topic}")
        self.console.print(f"🎯 只显示分组: {self.target_grouping}")
        self.console.print("[dim]等待订单簿数据...[/dim]\n")

        layout = Layout()
        layout.split_column(
            Layout(name="summary", size=15),
            Layout(name="orderbook")
        )

        with Live(layout, refresh_per_second=2, console=self.console) as live:
            try:
                for message in self.consumer:
                    orderbook_data = self.parse_orderbook_message(message.value)

                    if orderbook_data:
                        contract_id = orderbook_data['contract_id']

                        # 获取上一个状态
                        previous_orderbook = self.order_books.get(contract_id, {})
                        previous_bids = previous_orderbook.get('bids', {})
                        previous_asks = previous_orderbook.get('asks', {})

                        # 更新订单簿数据（只存储当前合约的数据）
                        self.order_books[contract_id] = orderbook_data

                        # 创建显示内容
                        summary_panel = self.create_summary_panel(orderbook_data)
                        orderbook_table = self.create_orderbook_table(
                            orderbook_data['bids'],
                            orderbook_data['asks'],
                            f"📈 实时订单簿 - 合约 {contract_id} (分组: {self.target_grouping})",
                            previous_bids,
                            previous_asks
                        )

                        # 更新布局
                        layout["summary"].update(summary_panel)
                        layout["orderbook"].update(orderbook_table)

            except KeyboardInterrupt:
                self.console.print("\n[bold yellow]🛑 停止实时显示[/bold yellow]")
            except Exception as e:
                self.console.print(f"[red]发生错误: {e}[/red]")
            finally:
                self.consumer.close()


def main():
    """主函数"""
    # 从环境变量获取配置，如果没有则使用默认值
    bootstrap_servers = os.getenv('KAFKA_BOOTSTRAP_SERVERS', 'localhost:9092')
    topic = os.getenv('KAFKA_TOPIC', 'api')
    target_grouping = float(os.getenv('ORDERBOOK_GROUPING', '0.1'))  # 默认显示 0.1 分组

    display = OrderBookDisplay(bootstrap_servers, topic, target_grouping)
    display.run()


if __name__ == "__main__":
    main()
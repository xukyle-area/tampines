#!/usr/bin/env python3

import json
import subprocess
import time
import sys

# [
#     {
#         "contractId": 1,
#         "price": 100.13,
#         "quantity": 10,
#         "side": "BID",
#         "action": "INSERT"
#     },
#     {
#         "contractId": 1,
#         "price": 100.10,
#         "quantity": 40,
#         "side": "BID",
#         "action": "DELETE"
#     }
# ]

def send_orders():
    """逐个发送订单到 Kafka"""

    topic = "order"
    bootstrap_servers = "localhost:9092"

    print("开始逐个发送订单到 Kafka topic:", topic)

    try:
        # 读取 JSON 文件
        with open('sample-orders.json', 'r') as f:
            orders = json.load(f)

        print(f"共找到 {len(orders)} 个订单")

        for i, order in enumerate(orders, 1):
            order_json = json.dumps(order, separators=(',', ':'))
            print(f"[{i}/{len(orders)}] 发送订单: {order_json}")

            # 使用 kafka-console-producer 发送
            process = subprocess.run(
                ['kafka-console-producer.sh', '--topic', topic, '--bootstrap-server', bootstrap_servers],
                input=order_json,
                text=True,
                capture_output=True
            )

            if process.returncode != 0:
                print(f"发送失败: {process.stderr}")
                sys.exit(1)

            # 小延迟避免发送过快
            time.sleep(0.1)

        print("所有订单发送完成")

    except FileNotFoundError:
        print("错误: 找不到 sample-orders.json 文件")
        sys.exit(1)
    except json.JSONDecodeError as e:
        print(f"错误: JSON 解析失败: {e}")
        sys.exit(1)
    except Exception as e:
        print(f"错误: {e}")
        sys.exit(1)

if __name__ == "__main__":
    send_orders()
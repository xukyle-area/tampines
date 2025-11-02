#!/bin/bash
# restart-flink-job.sh - 自动重启Flink作业并从最新checkpoint恢复

job_name="orderbook"
job_name_job="orderbook-job"
main_class="com.ganten.market.flink.job.OrderbookJob"
jar_file="market-flink/target/market-flink-1.0.0-SNAPSHOT.jar"

echo "=== Flink作业重启脚本 ==="
echo "作业名称: $job_name"
echo "主类: $main_class"
echo ""

# 1. 显示当前运行的作业
echo "当前运行的作业:"
flink list
echo ""

# 2. 查找并取消指定作业
echo "查找 $job_name_job 作业..."
JOB_INFOS=$(flink list | grep -i "$job_name_job")
if [ -n "$JOB_INFOS" ]; then
    echo "找到以下 $job_name_job 作业:"
    echo "$JOB_INFOS"
    echo ""

    # 逐个取消所有匹配的作业
    echo "$JOB_INFOS" | while read -r job_line; do
        if [ -n "$job_line" ]; then
            # 提取作业ID (格式: 时间 : job-id : job-name (status))
            JOB_ID=$(echo "$job_line" | awk -F' : ' '{print $2}' | tr -d ' ')

            if [ -n "$JOB_ID" ] && [[ $JOB_ID =~ ^[0-9a-fA-F]{32}$ ]]; then
                echo "正在取消作业ID: $JOB_ID"
                flink cancel "$JOB_ID"
                echo "作业 $JOB_ID 取消完成"
            else
                echo "跳过无效作业ID: $JOB_ID"
            fi
        fi
    done

    # 等待所有作业完全取消
    echo "等待所有作业取消完成..."
    sleep 5
else
    echo "没有找到运行中的 $job_name 作业，继续恢复流程..."
fi

echo ""

# 3. 获取最新的checkpoint目录（在作业取消完成后）
echo "查找最新的checkpoint..."
# 先找到最新的作业目录
LATEST_JOB_DIR=$(ls -t /tmp/flink-checkpoints/$job_name 2>/dev/null | head -1)

if [ -z "$LATEST_JOB_DIR" ]; then
    echo "错误: 没有找到checkpoint作业目录"
    exit 1
fi

# 在最新的作业目录中找到最新的checkpoint目录
LATEST_CHECKPOINT_DIR=$(ls -t "/tmp/flink-checkpoints/$job_name/$LATEST_JOB_DIR" 2>/dev/null | grep "^chk-" | head -1)

if [ -z "$LATEST_CHECKPOINT_DIR" ]; then
    echo "错误: 在作业目录 $LATEST_JOB_DIR 中没有找到checkpoint"
    exit 1
fi

LATEST_CHECKPOINT_DIR="/tmp/flink-checkpoints/$job_name/$LATEST_JOB_DIR/$LATEST_CHECKPOINT_DIR"

if [ ! -f "$LATEST_CHECKPOINT_DIR/_metadata" ]; then
    echo "错误: checkpoint目录 $LATEST_CHECKPOINT_DIR 中没有_metadata文件"
    exit 1
fi

echo "使用最新的checkpoint目录: $LATEST_CHECKPOINT_DIR"
echo ""

# 4. 从checkpoint恢复作业
echo "正在从checkpoint恢复作业..."
flink run -s "file://$LATEST_CHECKPOINT_DIR" \
    -c "$main_class" \
    "$jar_file" >/dev/null 2>&1 &
    echo "从checkpoint恢复成功！"
    sleep 5  # 等待作业完全启动

    if flink list | grep -q "$job_name_job.*RUNNING"; then
        echo "作业状态确认: 运行中"
    else
        echo "警告: 作业可能启动失败，请检查flink list"
    fi


echo ""
echo "作业重启完成！"
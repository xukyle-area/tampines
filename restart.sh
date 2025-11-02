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
# 查找所有作业目录中的最新checkpoint
JOB_DIRS=$(ls -t /tmp/flink-checkpoints/$job_name 2>/dev/null)

if [ -z "$JOB_DIRS" ]; then
    echo "错误: 没有找到任何checkpoint作业目录"
    HAS_CHECKPOINT=false
else
    LATEST_CHECKPOINT_DIR=""
    LATEST_CHECKPOINT_TIME=0

    for job_dir in $JOB_DIRS; do
        if [ -d "/tmp/flink-checkpoints/$job_name/$job_dir" ]; then
            POSSIBLE_CHECKPOINTS=$(ls -t "/tmp/flink-checkpoints/$job_name/$job_dir" 2>/dev/null)
            for chk_dir in $POSSIBLE_CHECKPOINTS; do
                chk_path="/tmp/flink-checkpoints/$job_name/$job_dir/$chk_dir"
                if [ -d "$chk_path" ] && [ -f "$chk_path/_metadata" ]; then
                    # 获取修改时间 (macOS使用stat -f %m, Linux使用stat -c %Y)
                    chk_time=$(stat -f %m "$chk_path" 2>/dev/null || stat -c %Y "$chk_path" 2>/dev/null || date -r "$chk_path" +%s 2>/dev/null || echo 0)
                    if [ "$chk_time" -gt "$LATEST_CHECKPOINT_TIME" ]; then
                        LATEST_CHECKPOINT_TIME=$chk_time
                        LATEST_CHECKPOINT_DIR="$chk_path"
                        echo "找到更新的checkpoint: $chk_path"
                    fi
                    break  # 每个作业目录只检查最新的checkpoint
                fi
            done
        fi
    done

    if [ -z "$LATEST_CHECKPOINT_DIR" ]; then
        echo "警告: 在所有作业目录中都没有找到有效的checkpoint目录"
        HAS_CHECKPOINT=false
    else
        HAS_CHECKPOINT=true
        echo "使用全局最新的checkpoint目录: $LATEST_CHECKPOINT_DIR"
    fi
fi
echo ""

# 4. 启动或恢复作业
if [ "$HAS_CHECKPOINT" = true ]; then
    echo "正在从checkpoint恢复作业..."
    flink run -s "file://$LATEST_CHECKPOINT_DIR" \
        -c "$main_class" \
        "$jar_file" >/dev/null 2>&1 &
    echo "从checkpoint恢复成功！"
else
    echo "正在启动新作业..."
    flink run -c "$main_class" \
        "$jar_file" >/dev/null 2>&1 &
    echo "新作业启动成功！"
fi

sleep 5  # 等待作业完全启动

# 获取新启动的作业ID
NEW_JOB_ID=$(flink list | grep "$job_name_job.*RUNNING" | awk -F' : ' '{print $2}' | tr -d ' ')
if [ -n "$NEW_JOB_ID" ]; then
    echo "新作业ID: $NEW_JOB_ID"
    echo "作业状态确认: 运行中"
else
    echo "警告: 作业可能启动失败"
    echo "当前Flink作业列表:"
    flink list
    echo ""
    echo "最近的作业运行历史:"
    flink list -r 2>/dev/null || echo "无法获取作业历史"
    echo ""
    echo "可能的解决方案:"
    echo "1. 检查Flink集群资源是否充足 (TaskManager数量和slot配置)"
    echo "2. 启动更多的TaskManager: ./bin/start-cluster.sh"
    echo "3. 检查Flink配置文件中的并行度设置"
    echo "4. 查看Flink Web UI了解集群状态"
fi


echo ""
echo "作业重启完成！"
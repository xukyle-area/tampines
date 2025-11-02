#!/bin/bash

git add .
if git diff --quiet && git diff --staged --quiet; then
    echo "代码没有变动，跳过构建步骤。"
else
    echo "检测到代码变动，开始构建..."
    cd market-common && mvn clean install -DskipTests && cd ..
    cd market-flink && mvn clean install -DskipTests && cd ..
fi
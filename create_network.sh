#!/bin/bash

NETWORK_NAME="data-analysis-network"

# 检查网络是否已存在
if ! docker network inspect "$NETWORK_NAME" &>/dev/null; then
    echo "Creating network $NETWORK_NAME..."
    docker network create \
        --driver bridge \
        --label project=data-analysis \
        --label environment=production \
        "$NETWORK_NAME"
    echo "Network created successfully!"
else
    echo "Network $NETWORK_NAME already exists."
fi
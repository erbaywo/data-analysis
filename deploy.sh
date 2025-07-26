# #!/bin/bash

# # 数据分析系统部署脚本
# # 适用于全新的Ubuntu/Debian服务器

# set -e

# echo "===== 开始部署数据分析系统 ====="

# # 更新系统包
# echo "正在更新系统包..."
# apt-get update
# apt-get upgrade -y

# # 安装必要的依赖
# echo "正在安装必要的依赖..."
# apt-get install -y \
#     apt-transport-https \
#     ca-certificates \
#     curl \
#     gnupg \
#     lsb-release \
#     git

# # 安装Docker
# echo "正在安装Docker..."
# curl -fsSL https://get.docker.com -o get-docker.sh
# sh get-docker.sh

# # 启动Docker服务
# echo "正在启动Docker服务..."
# systemctl enable docker
# systemctl start docker

# # 安装Docker Compose
# echo "正在安装Docker Compose..."
# curl -L "https://github.com/docker/compose/releases/download/v2.24.6/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
# chmod +x /usr/local/bin/docker-compose

# # 创建应用目录
# echo "正在创建应用目录..."
# mkdir -p /opt/data-analysis
# cd /opt/data-analysis

# # 克隆代码或复制文件
# echo "请选择部署方式："
# echo "1. 从Git仓库克隆代码"
# echo "2. 从本地上传代码"
# read -p "请输入选择(1/2): " deploy_choice

# if [ "$deploy_choice" == "1" ]; then
#     read -p "请输入Git仓库地址: " git_repo
#     git clone $git_repo .
# else
#     echo "请使用scp或其他工具将代码上传到服务器的/opt/data-analysis目录"
#     echo "例如: scp -r ./* user@your-server:/opt/data-analysis/"
#     echo "上传完成后按Enter继续..."
#     read
# fi

# # 确保脚本可执行
# chmod +x deploy.sh

# # 构建和启动容器
# echo "正在构建和启动容器..."
# docker-compose up -d

# # 等待应用启动
# echo "正在等待应用启动..."
# sleep 30

# # 检查应用状态
# echo "检查应用状态..."
# if curl -s http://localhost:9080/actuator/health | grep -q '"status":"UP"'; then
#     echo "===== 数据分析系统部署成功! ====="
#     echo "应用访问地址: http://$(hostname -I | awk '{print $1}'):9080"
# else
#     echo "===== 警告: 应用可能未正常启动，请检查日志 ====="
#     docker-compose logs app
# fi

# echo "可以使用以下命令查看应用日志："
# echo "docker-compose logs -f app"
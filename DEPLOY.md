# 数据分析系统部署指南

本文档提供了将数据分析系统部署到远程服务器的详细步骤。

## 系统要求

- 操作系统：Ubuntu 20.04+ 或 Debian 10+
- 最小配置：2核CPU，4GB内存，20GB存储空间
- 推荐配置：4核CPU，8GB内存，50GB存储空间
- 开放端口：9080（应用服务）


## 手动部署

如果您希望手动控制部署过程，请按照以下步骤操作：

### 步骤1：安装Docker和Docker Compose

```bash
# 安装Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# 安装Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/download/v2.24.6/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
```

### 步骤2：创建应用目录

```bash
sudo mkdir -p /opt/data-analysis
sudo chown $USER:$USER /opt/data-analysis
cd /opt/data-analysis
```

### 步骤3：上传项目文件

将整个项目上传到服务器：

```bash
# 在本地执行
scp -r ./* user@your-server-ip:/opt/data-analysis/
```


## 配置说明

### 环境变量

您可以通过修改`docker-compose.yml`文件中的环境变量来自定义配置：

```yaml
environment:
  - SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/data-analysis?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
  - SPRING_DATASOURCE_USERNAME=root
  - SPRING_DATASOURCE_PASSWORD=123456
```



# 部署步骤：构建和启动容器

##一、创建共享桥接网络

**脚本或者docker-compose方式二选一即可**
选择使用sh脚本创建

```bash
- 脚本文件添加可执行权限
chmod +x create_network.sh

- 执行脚本
./create_network.sh

```
- 查看网络是否创建成功
```bash
docker network ls
```

## 二、创建基础组件服务

```bash 
docker compose -f docker-compose.mysql.yml up -d
```

- 查询网络加入详情
```bash
docker network inspect data-analysis-network
```


### 数据持久化

数据库数据存储在Docker卷`mysql_data`中，确保数据持久化。

## 验证部署

部署完成后，可以通过以下URL访问应用：

```
http://your-server-ip:9080
```

健康检查接口：

```
http://your-server-ip:9080/actuator/health
```
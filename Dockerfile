# 多阶段构建 - 构建阶段（使用阿里云镜像）
FROM maven:3.8.7-openjdk-17-slim AS build
WORKDIR /app
# 复制自定义的Maven配置文件  
COPY settings.xml /root/.m2/settings.xml

# 复制 pom.xml 并下载依赖
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 复制源代码并构建
COPY src ./src
RUN mvn package -DskipTests -B

# 运行阶段
FROM openjdk:17-slim
WORKDIR /app

# 安装必要的工具
RUN apk add --no-cache curl tzdata && \
    cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone

# 创建非root用户
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# 复制构建好的jar包
COPY --from=build /app/target/*.jar app.jar

# 设置文件权限
RUN chown appuser:appgroup app.jar

# 切换到非root用户
USER appuser

# 设置JVM参数
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC"

# 暴露应用端口
EXPOSE 9080

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:9080/actuator/health || exit 1

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
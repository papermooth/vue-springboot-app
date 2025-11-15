# 部署说明

本文档详细说明如何部署和运行 Vue Spring Boot 后端服务。

## 环境要求

- JDK 17 或更高版本
- Maven 3.8+ 或 Gradle 7+
- MySQL 8.0+ (支持主从复制)
- Redis 6.0+ (单机模式或集群模式)
- 操作系统: Linux/Unix, macOS, Windows

## 数据库准备

### 1. 安装 MySQL

按照官方指南安装 MySQL 8.0 或更高版本。

### 2. 创建数据库

```sql
-- 在主库执行
CREATE DATABASE vue_springboot_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 创建用户
CREATE USER 'root'@'%' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON vue_springboot_db.* TO 'root'@'%';
FLUSH PRIVILEGES;
```

### 3. 配置主从复制

按照 MySQL 官方文档配置主从复制，将主库的 `vue_springboot_db` 数据库复制到从库。

## Redis 配置

### 1. 安装 Redis

按照官方指南安装 Redis 6.0 或更高版本。

### 2. 单机模式配置

默认配置文件 `redis.conf` 已适合大多数场景，确保以下配置项正确：

```
port 6379
database 16
auth your_redis_password  # 可选，如果需要密码认证
```

### 3. 启动 Redis

```bash
redis-server /path/to/redis.conf
```

## 应用配置

修改 `src/main/resources/application.properties` 文件中的配置项：

### 数据库配置

```properties
# 主库配置
spring.datasource.master.url=jdbc:mysql://主库IP:3306/vue_springboot_db?useSSL=false&serverTimezone=UTC&characterEncoding=utf-8
spring.datasource.master.username=your_username
spring.datasource.master.password=your_password

# 从库配置
spring.datasource.slave.url=jdbc:mysql://从库IP:3306/vue_springboot_db?useSSL=false&serverTimezone=UTC&characterEncoding=utf-8
spring.datasource.slave.username=your_username
spring.datasource.slave.password=your_password
```

### Redis 配置

```properties
# Redis 配置
spring.redis.host=127.0.0.1
spring.redis.port=6379
spring.redis.password=your_redis_password  # 如果有密码的话
spring.redis.database=0
spring.redis.timeout=60000
```

## 构建项目

### 使用 Maven 构建

```bash
cd /path/to/project/backend
mvn clean package -DskipTests
```

构建成功后，将在 `target` 目录生成 `demo-0.0.1-SNAPSHOT.jar` 文件。

## 运行项目

### 直接运行

```bash
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

### 使用 nohup 在后台运行

```bash
nohup java -jar target/demo-0.0.1-SNAPSHOT.jar > app.log 2>&1 &
```

### 使用 Docker 运行

创建 `Dockerfile`：

```dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/demo-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

构建并运行容器：

```bash
docker build -t vue-springboot-backend .
docker run -d -p 8080:8080 --name backend vue-springboot-backend
```

## 验证部署

服务启动后，可以通过以下方式验证：

1. 访问 API 文档：http://localhost:8080/swagger-ui/index.html
2. 检查服务日志：`tail -f app.log`
3. 测试基本 API 调用

## 健康检查

服务提供了健康检查端点：

```
http://localhost:8080/actuator/health
```

## 常见问题排查

1. **数据库连接失败**：检查数据库地址、用户名、密码是否正确，确保数据库服务运行正常
2. **Redis 连接失败**：检查 Redis 地址和端口，确保 Redis 服务运行正常
3. **端口冲突**：修改 `server.port` 配置项，使用其他未占用的端口
4. **内存不足**：使用 `-Xmx` 和 `-Xms` 参数调整 JVM 内存

## 性能监控

可以通过 Spring Boot Actuator 进行基本的性能监控：

```properties
# 在 application.properties 中启用
management.endpoints.web.exposure.include=health,info,metrics,prometheus
```

访问：http://localhost:8080/actuator/metrics 查看详细的性能指标。
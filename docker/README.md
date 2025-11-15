# 项目容器化配置说明

本目录包含了Vue + Spring Boot项目的Docker容器化配置，使用MySQL作为数据库，Redis集群作为缓存系统。

## 目录结构

```
docker/
├── Dockerfile-backend     # 后端服务Dockerfile
├── Dockerfile-frontend    # 前端服务Dockerfile
├── docker-compose.yml     # Docker Compose配置文件
├── .env                   # 环境变量配置
├── init-scripts/          # 初始化脚本
│   ├── init-db.sql        # MySQL数据库初始化脚本
│   └── create-redis-cluster.sh  # Redis集群初始化脚本
├── redis-conf/            # Redis配置文件
│   ├── redis-master-1.conf
│   ├── redis-master-2.conf
│   ├── redis-master-3.conf
│   ├── redis-replica-1.conf
│   ├── redis-replica-2.conf
│   └── redis-replica-3.conf
└── README.md              # 使用说明文档
```

## 组件说明

### 1. MySQL数据库集群
- 版本: MySQL 8.0
- 集群结构: 1主2从
- 主节点端口映射: 3306:3306
- 从节点1端口映射: 3307:3306
- 从节点2端口映射: 3308:3306
- 数据卷: mysql-master-data, mysql-slave-1-data, mysql-slave-2-data（持久化存储）
- 初始化脚本: 
  - init-db.sql（创建表结构和插入示例数据）
  - setup-replication.sh（配置主从复制）

### 2. Redis集群
- 版本: Redis 6.2
- 集群结构: 3主3从
- 端口范围: 7001-7006
- 初始化: create-redis-cluster.sh（自动创建集群）

### 3. 后端服务
- 基础镜像: OpenJDK 17
- 端口映射: 8080:8080
- 依赖: MySQL, Redis集群

### 4. 前端服务
- 构建镜像: Node.js 16
- 运行镜像: Nginx Alpine
- 端口映射: 80:80
- 依赖: 后端服务

## 使用方法

### 1. 构建后端JAR文件

在运行Docker容器前，请确保已构建后端JAR文件：

```bash
cd /root/vue-springboot-app/backend
mvn clean package
```

### 2. 启动所有服务

在docker目录下运行：

```bash
cd /root/vue-springboot-app/docker
docker-compose up -d
```

### 3. 访问应用

- 前端应用: http://localhost
- 后端API: http://localhost:8080
- MySQL主节点: localhost:3306
- MySQL从节点1: localhost:3307
- MySQL从节点2: localhost:3308
- Redis集群: localhost:7001-7006

### 4. 环境变量配置

可以通过修改`.env`文件自定义配置：

```
MYSQL_ROOT_PASSWORD=rootpassword
MYSQL_DATABASE=vue_springboot_db
MYSQL_USER=appuser
MYSQL_PASSWORD=apppassword
```

### 5. 停止服务

```bash
docker-compose down
```

### 6. 查看日志

```bash
# 查看所有服务日志
docker-compose logs

# 查看特定服务日志
docker-compose logs backend
```

## 注意事项

1. MySQL集群和Redis集群初始化需要一定时间，请确保服务完全启动
2. 首次启动时，数据库会自动初始化，主从复制配置会自动完成
3. 确保Docker和Docker Compose已正确安装
4. 如需修改配置，请在重新启动服务前停止现有容器
5. 后端服务默认连接到MySQL主节点进行写操作
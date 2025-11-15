# 容器架构说明文档

## 1. 架构概述

本项目采用Docker容器化部署，使用Docker Compose管理多个服务。整体架构采用经典的前后端分离设计，后端服务与数据库、缓存形成完整的技术栈。主要包含以下核心组件：

- **MySQL主从集群**：1主2从架构，实现数据高可用和读写分离
- **Redis集群**：3主3从架构，提供高性能缓存服务
- **后端服务**：基于Spring Boot开发的Java应用
- **前端服务**：基于Vue.js的单页应用，使用Nginx提供静态资源服务

## 2. 服务详情

### 2.1 MySQL数据库集群

#### 2.1.1 mysql-master（主节点）

**功能**：负责所有写操作，作为数据主源

- **镜像**：mysql:8.0
- **容器名称**：mysql-master
- **端口映射**：`3306:3306`（宿主机:容器）
- **数据卷**：
  - `mysql-master-data`：持久化MySQL数据
  - `./init-scripts`：初始化脚本目录
  - `./mysql-conf/master.cnf`：主节点配置文件
  - `./init-db.sql`：数据库初始化SQL
- **环境变量**：
  - `MYSQL_ROOT_PASSWORD`：root用户密码
  - `MYSQL_DATABASE`：应用数据库名
  - `MYSQL_USER`：应用连接数据库的用户名
  - `MYSQL_PASSWORD`：应用连接数据库的密码
- **关键配置**（master.cnf）：
  - 服务器ID：1
  - 启用二进制日志：`log-bin=mysql-bin`
  - 二进制日志格式：`binlog-format=ROW`
  - 自动递增设置：`auto-increment-increment=1`, `auto-increment-offset=1`

#### 2.1.2 mysql-slave-1/2（从节点）

**功能**：负责读操作，从主节点同步数据

- **镜像**：mysql:8.0
- **容器名称**：mysql-slave-1/mysql-slave-2
- **端口映射**：`3307:3306`/`3308:3306`
- **数据卷**：
  - `mysql-slave-1-data`/`mysql-slave-2-data`：持久化从节点数据
  - `./mysql-conf/slave.cnf`：从节点配置文件
- **依赖**：依赖mysql-master服务

#### 2.1.3 mysql-replication-init（复制初始化）

**功能**：一次性服务，配置MySQL主从复制关系

- **镜像**：mysql:8.0
- **入口脚本**：`setup-replication-new.sh`
- **初始化流程**：
  1. 在主节点创建复制用户`repl_user`
  2. 获取主节点二进制日志信息
  3. 配置从节点连接到主节点
  4. 验证复制状态

### 2.2 Redis缓存集群

#### 2.2.1 Redis主节点（redis-master-1/2/3）

**功能**：集群主节点，提供写操作和部分读操作

- **镜像**：redis:6.2-alpine
- **端口**：7001/7002/7003
- **配置文件**：
  - 端口配置
  - 绑定地址：0.0.0.0
  - 关闭保护模式：`protected-mode no`
  - 开启AOF持久化：`appendonly yes`

#### 2.2.2 Redis从节点（redis-replica-1/2/3）

**功能**：集群从节点，提供读操作和高可用

- **镜像**：redis:6.2-alpine
- **端口**：7004/7005/7006
- **配置**：与主节点类似，但在集群初始化后会被配置为对应主节点的从节点

#### 2.2.3 redis-cluster-init（集群初始化）

**功能**：一次性服务，创建Redis集群

- **镜像**：redis:6.2-alpine
- **入口脚本**：`create-redis-cluster.sh`
- **初始化流程**：
  - 使用`redis-cli --cluster create`命令创建集群
  - 配置3主3从架构（--cluster-replicas 1）

### 2.3 后端服务（backend）

**功能**：提供RESTful API，处理业务逻辑

- **构建文件**：`Dockerfile-backend`
- **基础镜像**：openjdk:17.0.2-slim
- **工作目录**：`/app`
- **部署文件**：`demo-0.0.1-SNAPSHOT.jar`
- **端口**：`8080`
- **依赖服务**：mysql-master, redis-cluster-init
- **环境变量**：
  - 数据库连接信息
  - Redis连接信息
  - Spring配置文件激活：`SPRING_PROFILES_ACTIVE=docker`

### 2.4 前端服务（frontend）

**功能**：提供用户界面，静态资源服务

- **构建文件**：`Dockerfile-frontend`（多阶段构建）
- **构建阶段**：
  - 基础镜像：node:16-alpine
  - 安装依赖：`npm install`
  - 构建项目：`npm run build`
- **部署阶段**：
  - 基础镜像：nginx:alpine
  - 部署目录：`/usr/share/nginx/html`
  - 端口：`80`
- **依赖服务**：backend

## 3. 网络配置

- **网络名称**：`app-network`
- **网络驱动**：bridge
- **功能**：所有服务都连接到此网络，实现容器间通信
- **服务发现**：容器间可通过服务名称（如mysql-master）相互访问

## 4. 数据卷配置

项目使用Docker命名卷持久化数据：

| 数据卷名称 | 关联服务 | 用途 |
|------------|----------|------|
| mysql-master-data | mysql-master | 持久化主节点MySQL数据 |
| mysql-slave-1-data | mysql-slave-1 | 持久化从节点1MySQL数据 |
| mysql-slave-2-data | mysql-slave-2 | 持久化从节点2MySQL数据 |

## 5. 环境变量配置

项目使用`.env`文件管理环境变量，主要配置项：

- **MySQL配置**：
  - `MYSQL_ROOT_PASSWORD`: root密码
  - `MYSQL_DATABASE`: 数据库名
  - `MYSQL_USER`: 应用用户名
  - `MYSQL_PASSWORD`: 应用用户密码

- **Redis集群配置**：
  - `REDIS_CLUSTER_NODES`: 集群节点列表

- **应用配置**：
  - `SPRING_PROFILES_ACTIVE`: Spring配置文件激活

## 6. 部署与运行

### 6.1 前置条件

- Docker版本：20.10+
- Docker Compose版本：1.29+
- 后端代码已构建（`./backend/target/demo-0.0.1-SNAPSHOT.jar`存在）

### 6.2 启动服务

在`docker`目录下执行：

```bash
docker-compose up -d
```

### 6.3 停止服务

```bash
docker-compose down
```

### 6.4 查看服务状态

```bash
docker-compose ps
```

### 6.5 查看服务日志

```bash
docker-compose logs [服务名称]
```

## 7. 数据安全与备份

### 7.1 MySQL数据备份

可以使用以下命令备份MySQL数据：

```bash
docker exec mysql-master mysqldump -uroot -p${MYSQL_ROOT_PASSWORD} ${MYSQL_DATABASE} > backup.sql
```

### 7.2 Redis数据备份

Redis集群数据通过AOF文件持久化，位于容器的数据目录中。

## 8. 性能优化

### 8.1 MySQL性能优化

- 使用主从复制实现读写分离
- 根据实际负载调整连接池大小
- 定期清理过期的二进制日志

### 8.2 Redis性能优化

- 集群模式提供高并发支持
- AOF持久化确保数据安全
- 根据业务需求调整内存策略

## 9. 常见问题与故障排查

### 9.1 MySQL主从复制问题

- **症状**：从节点数据不同步
- **检查**：`docker exec mysql-slave-1 mysql -uroot -prootpassword -e "SHOW SLAVE STATUS\G"`
- **常见错误**：
  - 复制用户权限问题
  - 二进制日志文件位置不匹配
  - 从节点表结构不一致

### 9.2 Redis集群问题

- **症状**：缓存读写失败
- **检查**：`docker exec redis-master-1 redis-cli -p 7001 cluster info`
- **常见错误**：
  - 集群节点未完全启动
  - 网络连接问题

### 9.3 服务启动顺序

服务间存在依赖关系，请确保按照以下顺序启动：
1. MySQL和Redis基础服务
2. 初始化服务（mysql-replication-init, redis-cluster-init）
3. 后端服务
4. 前端服务

## 10. 注意事项

1. 生产环境中应修改默认密码
2. 生产环境建议启用MySQL从节点的只读模式
3. 定期备份关键数据
4. 根据实际负载调整容器资源限制
5. 监控系统性能，及时优化配置

## 11. 升级与维护

### 11.1 服务升级

1. 构建新的后端JAR包
2. 更新Docker镜像：`docker-compose up -d --build [服务名称]`
3. 验证服务运行状态

### 11.2 数据库升级

1. 在主节点执行数据库迁移脚本
2. 等待从节点自动同步
3. 验证数据一致性

---

文档版本：v1.0
创建日期：2025-11-16
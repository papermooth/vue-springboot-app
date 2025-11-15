# Vue Spring Boot 后端项目

## 项目概述

这是一个基于 Spring Boot 3 的后端服务项目，提供用户管理、产品管理和订单管理等核心功能，支持数据库读写分离和 Redis 缓存，采用现代化的 Java 开发技术栈。

## 技术栈

- **框架**: Spring Boot 3.1.0
- **ORM**: Spring Data JPA
- **数据库**: MySQL 8.x (主从架构)
- **缓存**: Redis
- **连接池**: HikariCP
- **构建工具**: Maven
- **编程语言**: Java 17
- **API文档**: SpringDoc OpenAPI

## 功能特性

- ✅ 用户管理 (注册、登录、CRUD)
- ✅ 产品管理 (CRUD、库存管理)
- ✅ 订单管理 (创建订单、状态更新、查询)
- ✅ Redis 缓存支持
- ✅ 数据库读写分离
- ✅ HikariCP 连接池优化
- ✅ 异常处理和全局响应格式
- ✅ CORS 跨域支持

## 项目结构

```
src/main/java/com/example/demo/
├── VueSpringbootBackendApplication.java  # 应用入口
├── config/                               # 配置类
│   ├── DataSourceAspect.java             # 数据源切换切面
│   ├── DataSourceConfig.java             # 数据源配置
│   ├── DataSourceContextHolder.java      # 数据源上下文
│   ├── DataSourceType.java               # 数据源类型枚举
│   └── RedisConfig.java                  # Redis配置
├── controller/                           # 控制器
├── exception/                            # 异常处理
├── model/                                # 实体类
├── repository/                           # 数据访问层
├── service/                              # 业务逻辑层
│   └── impl/                             # 实现类
└── util/                                 # 工具类
```

## 系统架构

### 数据库架构
- **主从架构**: 主库处理写操作，从库处理读操作
- **连接池**: 使用 HikariCP 作为高性能连接池

### 缓存架构
- **Redis 缓存**: 用于缓存热点数据和管理用户会话
- **缓存策略**: 采用多级缓存策略，支持自动失效和手动清除

### 数据源路由
- **AOP 实现**: 通过 AOP 切面自动切换读写数据源
- **负载均衡**: 读操作支持简单的负载均衡策略
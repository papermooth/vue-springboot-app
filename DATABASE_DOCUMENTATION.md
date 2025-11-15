# Vue-SpringBoot 应用数据库说明文档

## 1. 数据库概述

本文档详细描述了Vue-SpringBoot电商应用的数据库设计，包括表结构、字段说明、表间关系、索引约束以及性能优化建议。该数据库采用关系型设计，支持用户管理、产品管理、订单管理等核心业务功能。

## 2. 数据库表结构

系统包含以下四个主要数据表：

| 表名 | 中文名称 | 主要功能 |
|------|---------|----------|
| `users` | 用户表 | 存储用户基本信息 |
| `products` | 产品表 | 存储商品信息 |
| `orders` | 订单表 | 存储订单主信息 |
| `order_items` | 订单项表 | 存储订单详细商品信息 |

## 3. 详细表结构说明

### 3.1 users 表（用户表）

```sql
CREATE TABLE IF NOT EXISTS users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL UNIQUE,
  password VARCHAR(100) NOT NULL,
  email VARCHAR(100) NOT NULL UNIQUE,
  full_name VARCHAR(100),
  phone VARCHAR(20),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

**字段说明：**

| 字段名 | 数据类型 | 约束 | 描述 |
|-------|---------|------|------|
| `id` | BIGINT | AUTO_INCREMENT, PRIMARY KEY | 用户ID，自增长主键 |
| `username` | VARCHAR(50) | NOT NULL, UNIQUE | 用户名，不允许重复 |
| `password` | VARCHAR(100) | NOT NULL | 密码（建议加密存储） |
| `email` | VARCHAR(100) | NOT NULL, UNIQUE | 邮箱地址，不允许重复 |
| `full_name` | VARCHAR(100) | NULL | 用户全名 |
| `phone` | VARCHAR(20) | NULL | 手机号码 |
| `created_at` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | 创建时间，自动记录 |
| `updated_at` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间，自动更新 |

### 3.2 products 表（产品表）

```sql
CREATE TABLE IF NOT EXISTS products (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  description TEXT,
  price DECIMAL(10,2) NOT NULL,
  stock INT NOT NULL DEFAULT 0,
  category VARCHAR(50),
  image_url VARCHAR(255),
  status VARCHAR(20) NOT NULL DEFAULT 'true',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

**字段说明：**

| 字段名 | 数据类型 | 约束 | 描述 |
|-------|---------|------|------|
| `id` | BIGINT | AUTO_INCREMENT, PRIMARY KEY | 产品ID，自增长主键 |
| `name` | VARCHAR(100) | NOT NULL | 产品名称 |
| `description` | TEXT | NULL | 产品详细描述 |
| `price` | DECIMAL(10,2) | NOT NULL | 产品价格，精确到2位小数 |
| `stock` | INT | NOT NULL DEFAULT 0 | 库存数量，默认0 |
| `category` | VARCHAR(50) | NULL | 产品分类 |
| `image_url` | VARCHAR(255) | NULL | 产品图片URL |
| `status` | VARCHAR(20) | NOT NULL DEFAULT 'true' | 产品状态（true/false，建议改为BOOLEAN类型） |
| `created_at` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | 创建时间，自动记录 |
| `updated_at` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间，自动更新 |

### 3.3 orders 表（订单表）

```sql
CREATE TABLE IF NOT EXISTS orders (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  order_no VARCHAR(50) NOT NULL UNIQUE,
  user_id BIGINT NOT NULL,
  total_amount DECIMAL(10,2) NOT NULL,
  total_quantity INT DEFAULT 0,
  status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
  shipping_address VARCHAR(255),
  payment_method VARCHAR(50),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id)
);
```

**字段说明：**

| 字段名 | 数据类型 | 约束 | 描述 |
|-------|---------|------|------|
| `id` | BIGINT | AUTO_INCREMENT, PRIMARY KEY | 订单ID，自增长主键 |
| `order_no` | VARCHAR(50) | NOT NULL, UNIQUE | 订单编号，唯一标识 |
| `user_id` | BIGINT | NOT NULL, FOREIGN KEY | 用户ID，关联users表 |
| `total_amount` | DECIMAL(10,2) | NOT NULL | 订单总金额 |
| `total_quantity` | INT | DEFAULT 0 | 订单商品总数量 |
| `status` | VARCHAR(20) | NOT NULL DEFAULT 'PENDING' | 订单状态（PENDING, COMPLETED, CANCELLED等） |
| `shipping_address` | VARCHAR(255) | NULL | 收货地址 |
| `payment_method` | VARCHAR(50) | NULL | 支付方式 |
| `created_at` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | 创建时间，自动记录 |
| `updated_at` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间，自动更新 |

### 3.4 order_items 表（订单项表）

```sql
CREATE TABLE IF NOT EXISTS order_items (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  order_id BIGINT NOT NULL,
  product_id BIGINT NOT NULL,
  product_name VARCHAR(255) NOT NULL,
  quantity INT NOT NULL,
  price DECIMAL(10,2) NOT NULL,
  subtotal DECIMAL(10,2) NOT NULL,
  FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
  FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);
```

**字段说明：**

| 字段名 | 数据类型 | 约束 | 描述 |
|-------|---------|------|------|
| `id` | BIGINT | AUTO_INCREMENT, PRIMARY KEY | 订单项ID，自增长主键 |
| `order_id` | BIGINT | NOT NULL, FOREIGN KEY ON DELETE CASCADE | 订单ID，关联orders表，级联删除 |
| `product_id` | BIGINT | NOT NULL, FOREIGN KEY ON DELETE CASCADE | 产品ID，关联products表，级联删除 |
| `product_name` | VARCHAR(255) | NOT NULL | 产品名称（冗余字段，便于查询） |
| `quantity` | INT | NOT NULL | 购买数量 |
| `price` | DECIMAL(10,2) | NOT NULL | 购买时的单价 |
| `subtotal` | DECIMAL(10,2) | NOT NULL | 该商品小计金额（price * quantity） |

## 4. 表间关系说明

数据库采用了标准的关系型设计，表之间通过外键建立关联，形成清晰的实体关系模型：

### 4.1 关系图

```
users (1) ---------------< (N) orders (1) ---------------< (N) order_items (N) >--------------- (1) products
```

### 4.2 详细关系说明

1. **用户与订单关系**（一对多）：
   - 一个用户可以创建多个订单
   - 一个订单只能属于一个用户
   - 通过`orders.user_id`关联`users.id`

2. **订单与订单项关系**（一对多）：
   - 一个订单可以包含多个订单项
   - 一个订单项只能属于一个订单
   - 通过`order_items.order_id`关联`orders.id`
   - 采用级联删除策略：删除订单时，相关的订单项自动删除

3. **产品与订单项关系**（一对多）：
   - 一个产品可以出现在多个订单项中
   - 一个订单项只对应一个产品
   - 通过`order_items.product_id`关联`products.id`
   - 采用级联删除策略：删除产品时，相关的订单项自动删除（实际业务中可能需要考虑软删除）

## 5. 索引和约束

### 5.1 主键索引

所有表都定义了`id`字段作为主键，自动创建主键索引，确保记录的唯一性和快速检索。

### 5.2 唯一约束

- `users.username`：确保用户名唯一
- `users.email`：确保邮箱地址唯一
- `orders.order_no`：确保订单编号唯一

### 5.3 外键约束

- `orders.user_id` → `users.id`
- `order_items.order_id` → `orders.id` (ON DELETE CASCADE)
- `order_items.product_id` → `products.id` (ON DELETE CASCADE)

### 5.4 默认值约束

多个字段设置了默认值，例如：
- `products.stock`：默认值0
- `products.status`：默认值'true'
- `orders.status`：默认值'PENDING'
- 所有`created_at`和`updated_at`字段都设置了时间戳默认值

## 6. 数据类型选择

| 字段类型 | 使用场景 | 选择理由 |
|---------|---------|---------|
| BIGINT | 主键ID | 提供足够大的范围，适合高并发系统 |
| VARCHAR | 字符串数据 | 可变长度，节省空间，适合用户名、邮箱等 |
| TEXT | 长文本 | 适合存储产品详细描述等大型文本 |
| DECIMAL(10,2) | 金额数据 | 精确存储，避免浮点数精度问题 |
| INT | 整数数据 | 适合数量、库存等整数信息 |
| TIMESTAMP | 时间信息 | 自动记录时间，支持时区转换 |

## 7. 数据库设计建议与优化方案

### 7.1 数据类型优化

1. **产品状态字段优化**
   - 当前设计：`status VARCHAR(20) NOT NULL DEFAULT 'true'`
   - 建议优化：`status BOOLEAN NOT NULL DEFAULT TRUE`
   - 理由：布尔类型占用空间更小，查询效率更高，语义更明确

2. **时间戳优化**
   - 考虑使用`DATETIME`替代`TIMESTAMP`，如果需要存储更广泛的时间范围
   - `TIMESTAMP`有时间范围限制（1970-01-01 00:00:01 到 2038-01-19 03:14:07 UTC）

### 7.2 索引优化

1. **添加必要索引**
   - 为`orders.user_id`添加索引，优化用户订单查询
   - 为`order_items.order_id`添加索引，优化订单详情查询
   - 为`products.category`添加索引，优化分类查询
   - 为`orders.status`添加索引，优化订单状态筛选

2. **索引创建示例**
   ```sql
   -- 为常用查询字段添加索引
   CREATE INDEX idx_orders_user_id ON orders(user_id);
   CREATE INDEX idx_order_items_order_id ON order_items(order_id);
   CREATE INDEX idx_products_category ON products(category);
   CREATE INDEX idx_orders_status ON orders(status);
   ```

### 7.3 约束优化

1. **级联删除策略调整**
   - 当前：`ON DELETE CASCADE`对产品表和订单表
   - 建议：对于产品表，考虑使用软删除或限制删除，避免订单历史数据丢失
   - 示例修改：
     ```sql
     -- 修改级联策略为限制删除
     ALTER TABLE order_items 
     DROP FOREIGN KEY [当前外键名];
     
     ALTER TABLE order_items 
     ADD CONSTRAINT fk_order_items_product 
     FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE RESTRICT;
     ```

2. **添加检查约束**
   - 为`price`、`stock`、`quantity`等数值字段添加检查约束，确保数据有效性
   - 示例：
     ```sql
     ALTER TABLE products ADD CHECK (price >= 0);
     ALTER TABLE products ADD CHECK (stock >= 0);
     ALTER TABLE order_items ADD CHECK (quantity > 0);
     ```

### 7.4 性能优化建议

1. **查询优化**
   - 避免使用`SELECT *`，只查询必要字段
   - 对于频繁连接查询，考虑使用视图
   - 大表查询时使用分页

2. **事务管理**
   - 订单创建和库存扣减应在同一事务中处理
   - 事务应尽量简短，减少锁定时间

3. **缓存策略**
   - 对热门产品信息使用缓存
   - 考虑使用Redis缓存频繁查询的数据

### 7.5 数据安全建议

1. **密码安全**
   - 密码字段应使用哈希算法存储，如BCrypt
   - 修改当前表结构：
     ```sql
     ALTER TABLE users MODIFY COLUMN password VARCHAR(255) NOT NULL; -- 增加长度以存储哈希值
     ```

2. **数据备份**
   - 定期进行数据库备份
   - 实现增量备份策略

3. **访问控制**
   - 为数据库用户设置最小权限原则
   - 应用层实现细粒度的权限控制

### 7.6 扩展性考虑

1. **分表分库策略**
   - 订单数据量大时可按时间范围分表
   - 用户数据可考虑按地域分库

2. **历史数据归档**
   - 设计历史订单表，定期将老订单归档
   - 示例归档表结构：
     ```sql
     CREATE TABLE order_history LIKE orders;
     CREATE TABLE order_items_history LIKE order_items;
     ```

## 8. 数据库迁移与维护

### 8.1 迁移建议

1. 使用专业的数据库迁移工具（如Flyway或Liquibase）管理数据库版本
2. 编写清晰的迁移脚本，包含版本号和描述
3. 在测试环境充分测试后再应用到生产环境

### 8.2 维护建议

1. **定期监控**
   - 监控慢查询日志
   - 监控表空间使用情况
   - 监控连接数和性能指标

2. **定期维护任务**
   - 优化表：`OPTIMIZE TABLE`
   - 更新统计信息：`ANALYZE TABLE`
   - 检查和修复表：`CHECK TABLE`, `REPAIR TABLE`

## 9. 总结

本数据库设计采用关系型设计范式，结构清晰，关系明确，支持电商应用的核心功能。通过实施建议的优化方案，可以进一步提升数据库性能、安全性和可扩展性。在系统演进过程中，应根据实际业务需求和数据量变化，持续优化数据库设计。
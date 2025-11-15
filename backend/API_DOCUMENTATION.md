# API 文档

本文档详细说明了 Vue Spring Boot 后端服务提供的 API 接口。

## 基础信息

- **服务地址**: http://localhost:8080
- **API 前缀**: 无特殊前缀
- **认证方式**: 目前未实现认证，生产环境建议集成 JWT
- **请求/响应格式**: JSON

## 用户管理 API

### 1. 获取所有用户

```
GET /users
```

**响应示例**:
```json
[
  {
    "id": 1,
    "username": "admin",
    "email": "admin@example.com",
    "createdAt": "2024-01-01T10:00:00Z"
  }
]
```

### 2. 获取单个用户

```
GET /users/{id}
```

**路径参数**:
- `id`: 用户 ID

**响应示例**:
```json
{
  "id": 1,
  "username": "admin",
  "email": "admin@example.com",
  "createdAt": "2024-01-01T10:00:00Z"
}
```

### 3. 创建用户

```
POST /users
```

**请求体**:
```json
{
  "username": "newuser",
  "email": "newuser@example.com",
  "password": "password123"
}
```

**响应示例**:
```json
{
  "id": 2,
  "username": "newuser",
  "email": "newuser@example.com",
  "createdAt": "2024-01-01T11:00:00Z"
}
```

### 4. 更新用户

```
PUT /users/{id}
```

**路径参数**:
- `id`: 用户 ID

**请求体**:
```json
{
  "email": "updated@example.com"
}
```

**响应示例**:
```json
{
  "id": 1,
  "username": "admin",
  "email": "updated@example.com",
  "createdAt": "2024-01-01T10:00:00Z"
}
```

### 5. 删除用户

```
DELETE /users/{id}
```

**路径参数**:
- `id`: 用户 ID

**响应示例**:
```
204 No Content
```

## 产品管理 API

### 1. 获取所有产品

```
GET /products
```

**响应示例**:
```json
[
  {
    "id": 1,
    "name": "Product 1",
    "description": "Description 1",
    "price": 100.00,
    "stock": 10,
    "category": "Electronics"
  }
]
```

### 2. 获取单个产品

```
GET /products/{id}
```

**路径参数**:
- `id`: 产品 ID

### 3. 创建产品

```
POST /products
```

**请求体**:
```json
{
  "name": "New Product",
  "description": "Product Description",
  "price": 99.99,
  "stock": 50,
  "category": "Clothing"
}
```

### 4. 更新产品

```
PUT /products/{id}
```

**路径参数**:
- `id`: 产品 ID

### 5. 删除产品

```
DELETE /products/{id}
```

**路径参数**:
- `id`: 产品 ID

### 6. 根据分类获取产品

```
GET /products/category/{category}
```

**路径参数**:
- `category`: 产品分类

### 7. 获取活跃产品（库存大于0）

```
GET /products/active
```

### 8. 搜索产品

```
GET /products/search?keyword=keyword
```

**查询参数**:
- `keyword`: 搜索关键词

## 订单管理 API

### 1. 获取所有订单

```
GET /orders
```

**响应示例**:
```json
[
  {
    "id": 1,
    "userId": 1,
    "totalAmount": 150.00,
    "status": "COMPLETED",
    "createdAt": "2024-01-01T12:00:00Z",
    "orderItems": [
      {
        "productId": 1,
        "quantity": 1,
        "price": 100.00
      }
    ]
  }
]
```

### 2. 获取单个订单

```
GET /orders/{id}
```

**路径参数**:
- `id`: 订单 ID

### 3. 创建订单

```
POST /orders
```

**请求体**:
```json
{
  "userId": 1,
  "orderItems": [
    {
      "productId": 1,
      "quantity": 1
    }
  ]
}
```

### 4. 更新订单状态

```
PUT /orders/{id}/status
```

**路径参数**:
- `id`: 订单 ID

**请求体**:
```json
{
  "status": "SHIPPED"
}
```

### 5. 取消订单

```
PUT /orders/{id}/cancel
```

**路径参数**:
- `id`: 订单 ID

### 6. 根据用户获取订单

```
GET /orders/user/{userId}
```

**路径参数**:
- `userId`: 用户 ID

### 7. 根据状态获取订单

```
GET /orders/status/{status}
```

**路径参数**:
- `status`: 订单状态（PENDING, COMPLETED, CANCELLED, SHIPPED）

## 错误响应格式

系统统一的错误响应格式：

```json
{
  "status": "error",
  "message": "错误描述",
  "timestamp": "2024-01-01T10:00:00Z"
}
```

## 状态码说明

- `200 OK`: 请求成功
- `201 Created`: 资源创建成功
- `204 No Content`: 请求成功但无响应体
- `400 Bad Request`: 请求参数错误
- `404 Not Found`: 资源不存在
- `500 Internal Server Error`: 服务器内部错误

## 使用 Swagger UI

系统集成了 Swagger UI，可以通过以下地址访问交互式 API 文档：

```
http://localhost:8080/swagger-ui/index.html
```

在 Swagger UI 中，您可以查看所有可用的 API，测试接口，以及获取详细的请求/响应格式说明。
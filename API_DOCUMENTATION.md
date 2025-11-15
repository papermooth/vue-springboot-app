# Vue-SpringBoot 应用 API 文档

## 接口基础信息

- **API基础路径**: `http://localhost:8080/api`
- **重要说明**: 所有API端点都必须包含`/api`前缀，例如`/api/users`而不是`/users`，否则会返回404错误
- **请求方式**: GET, POST, PUT, DELETE
- **数据格式**: JSON
- **响应格式**: 
  - 成功: `{"code": 200, "data": {...}, "message": "Success"}`
  - 失败: `{"message": "错误信息", "details": "详细信息", "timestamp": "时间戳"}`
  - 404错误: `{"timestamp": "2025-11-15T16:00:56.000+00:00", "status": 404, "error": "Not Found", "path": "/users"}` (当URL不包含`/api`前缀时返回此错误)

## 1. 用户相关接口

### 1.1 创建用户

**URL**: `/api/users`
**方法**: `POST`
**请求体**:
```json
{
  "username": "string",      // 用户名（必填）
  "password": "string",      // 密码（必填）
  "email": "string",         // 邮箱（必填）
  "fullName": "string",      // 全名（必填）
  "phone": "string"          // 手机号（必填）
}
```

**成功响应**:
```json
{
  "id": 1,
  "username": "testuser",
  "password": "password123",
  "email": "test@example.com",
  "phone": "1234567890",
  "fullName": "Test User",
  "createdAt": "2025-11-15T15:17:20.000+00:00",
  "updatedAt": "2025-11-15T15:17:20.000+00:00"
}
```

### 1.2 获取用户列表

**URL**: `/api/users`
**方法**: `GET`
**成功响应**:
```json
[
  {
    "id": 1,
    "username": "testuser",
    "password": "password123",
    "email": "test@example.com",
    "phone": "1234567890",
    "fullName": "Test User",
    "createdAt": "2025-11-15T15:17:20.000+00:00",
    "updatedAt": "2025-11-15T15:17:20.000+00:00"
  }
]
```

### 1.3 获取单个用户

**URL**: `/api/users/{id}`
**方法**: `GET`
**路径参数**:
- `id`: 用户ID

**成功响应**:
```json
{
  "id": 1,
  "username": "testuser",
  "password": "password123",
  "email": "test@example.com",
  "phone": "1234567890",
  "fullName": "Test User"
}
```

### 1.4 更新用户

**URL**: `/api/users/{id}`
**方法**: `PUT`
**路径参数**:
- `id`: 用户ID
**请求体**:
```json
{
  "email": "string",      // 可选
  "fullName": "string",   // 可选
  "phone": "string"       // 可选
}
```

**注意**: 当前实现存在问题，返回500错误。

### 1.5 删除用户

**URL**: `/api/users/{id}`
**方法**: `DELETE`
**路径参数**:
- `id`: 用户ID

**成功响应**:
- 无明确响应体，但删除成功后用户列表中不再包含该用户

## 2. 产品相关接口

### 2.1 创建产品

**URL**: `/api/products`
**方法**: `POST`
**请求体**:
```json
{
  "name": "string",          // 产品名称（必填）
  "price": 99.99,            // 价格（必填）
  "stock": 100,              // 库存（必填）
  "description": "string",   // 描述（必填）
  "status": true,            // 状态（必填）
  "category": "string",     // 分类（必填）
  "imageUrl": null           // 图片URL（可选）
}
```

**成功响应**:
```json
{
  "code": 200,
  "data": {
    "id": 1,
    "name": "Test Product",
    "description": "Test Description",
    "price": 99.99,
    "stock": 100,
    "imageUrl": null,
    "category": "Electronics",
    "status": true,
    "createdAt": "2025-11-15T15:17:34.000+00:00",
    "updatedAt": "2025-11-15T15:17:34.000+00:00"
  }
}
```

### 2.2 获取产品列表

**URL**: `/api/products`
**方法**: `GET`
**成功响应**:
```json
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "name": "Test Product",
      "description": "Test Description",
      "price": 99.99,
      "stock": 99,
      "imageUrl": null,
      "category": "Electronics",
      "status": true,
      "createdAt": "2025-11-15T15:17:34.000+00:00",
      "updatedAt": "2025-11-15T15:17:49.000+00:00"
    }
  ]
}
```

### 2.3 获取单个产品

**URL**: `/api/products/{id}`
**方法**: `GET`
**路径参数**:
- `id`: 产品ID

**成功响应**:
```json
{
  "code": 200,
  "data": {
    "id": 1,
    "name": "Test Product",
    "description": "Test Description",
    "price": 99.99,
    "stock": 99,
    "imageUrl": null,
    "category": "Electronics",
    "status": true,
    "createdAt": "2025-11-15T15:17:34.000+00:00",
    "updatedAt": "2025-11-15T15:17:49.000+00:00"
  }
}
```

### 2.4 更新产品

**URL**: `/api/products/{id}`
**方法**: `PUT`
**路径参数**:
- `id`: 产品ID
**请求体**:
```json
{
  "price": 189.99,           // 可选
  "stock": 150,              // 可选
  "description": "string",   // 可选
  "status": true,            // 可选
  "category": "string"       // 可选
}
```

**注意**: 当前实现存在问题，返回500错误。

### 2.5 删除产品

**URL**: `/api/products/{id}`
**方法**: `DELETE`
**路径参数**:
- `id`: 产品ID

**成功响应**:
```json
{
  "code": 200,
  "message": "Success"
}
```

## 3. 订单相关接口

### 3.1 创建订单

**URL**: `/api/orders`
**方法**: `POST`
**请求体**:
```json
{
  "userId": 1,              // 用户ID（必填）
  "totalAmount": 199.98,    // 总金额（必填）
  "totalQuantity": 2,       // 总数量（必填）
  "status": "PENDING",      // 订单状态（必填）
  "shippingAddress": "string", // 收货地址（必填）
  "paymentMethod": "PAYPAL",   // 支付方式（必填）
  "items": [                // 订单项列表（必填）
    {
      "productId": 1,       // 产品ID（必填）
      "productName": "Test Product", // 产品名称（必填）
      "quantity": 2,        // 数量（必填）
      "price": 99.99        // 单价（必填）
    }
  ]
}
```

**成功响应**:
```json
{
  "code": 200,
  "data": {
    "id": 1,
    "orderNo": "ORD326DEFBFFF",
    "user": {"id": 1, "username": null, /* 其他用户信息 */},
    "totalAmount": 199.98,
    "totalQuantity": 2,
    "status": "PENDING",
    "shippingAddress": "Test Address",
    "paymentMethod": "PAYPAL",
    "createdAt": "2025-11-15T15:22:27.000+00:00",
    "updatedAt": "2025-11-15T15:22:27.000+00:00"
  }
}
```

### 3.2 获取订单列表

**URL**: `/api/orders`
**方法**: `GET`
**成功响应**:
```json
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "orderNo": "ORD326DEFBFFF",
      "user": {
        "id": 1,
        "username": "testuser",
        "password": "password123",
        "email": "test@example.com",
        "phone": "1234567890",
        "fullName": "Test User"
      },
      "totalAmount": 99.99,
      "totalQuantity": 1,
      "status": "PENDING",
      "shippingAddress": "Test Address",
      "paymentMethod": "CREDIT_CARD",
      "createdAt": "2025-11-15T15:17:49.000+00:00",
      "updatedAt": "2025-11-15T15:17:49.000+00:00"
    }
  ]
}
```

### 3.3 获取订单详情

**URL**: `/api/orders/{id}`
**方法**: `GET`
**路径参数**:
- `id`: 订单ID

**成功响应**:
```json
{
  "code": 200,
  "data": {
    "items": [
      {
        "id": 1,
        "order": {/* 订单信息 */},
        "product": {/* 产品信息 */},
        "quantity": 1,
        "price": 99.99,
        "subtotal": 99.99,  // 自动计算: price * quantity
        "productName": "Test Product"
      }
    ],
    "order": {/* 订单信息 */}
  },
  "message": "Success"
}
```

### 3.4 更新订单

**URL**: `/api/orders/{id}`
**方法**: `PUT`
**路径参数**:
- `id`: 订单ID
**请求体**:
```json
{
  "status": "COMPLETED",      // 可选
  "shippingAddress": "string" // 可选
}
```

**注意**: 当前实现存在问题，返回500错误。

### 3.5 删除订单

**URL**: `/api/orders/{id}`
**方法**: `DELETE`
**路径参数**:
- `id`: 订单ID

**注意**: 当前实现存在问题，返回500错误。

## 4. 订单项管理

订单项通过订单接口进行管理，没有独立的CRUD接口。创建订单时同时创建订单项，删除订单时同时删除订单项。

## 5. 重要字段说明

### 5.1 订单状态枚举
- `PENDING`: 待处理
- `COMPLETED`: 已完成
- `CANCELLED`: 已取消
- `SHIPPED`: 已发货

### 5.2 支付方式枚举
- `CREDIT_CARD`: 信用卡
- `PAYPAL`: PayPal
- `BANK_TRANSFER`: 银行转账
- `CASH_ON_DELIVERY`: 货到付款

### 5.3 产品分类枚举
- `Electronics`: 电子产品
- `Books`: 图书
- `Clothing`: 服装
- `Food`: 食品
- `Others`: 其他

## 6. 接口测试结果汇总

| 接口类型 | 创建 | 查询列表 | 查询单个 | 更新 | 删除 |
|---------|------|---------|---------|------|------|
| 用户接口 | ✅ | ✅ | ✅ | ❌ | ✅ |
| 产品接口 | ✅ | ✅ | ✅ | ❌ | ✅ |
| 订单接口 | ✅ | ✅ | ✅ | ❌ | ❌ |
| 订单项接口 | ❌ (通过订单管理) | ❌ | ❌ | ❌ | ❌ |

**重要注意事项**:
- ✅: 功能正常
- ❌: 存在问题或不支持
- **关键错误**: 如果请求URL不包含`/api`前缀（如直接使用`/users`而不是`/api/users`），所有接口将返回404错误
- 更新和删除操作存在问题，需要进一步修复
- 正确的API调用示例: `http://localhost:8080/api/users`
- 错误的API调用示例: `http://localhost:8080/users` (将返回404错误)
- 订单项通过订单接口进行管理，无需单独操作。

## 概述

本文档详细描述了 Vue-SpringBoot 应用的所有后端 API 端点，包括用户管理、产品管理和订单管理相关的接口。

## 基础信息

- **API 基础路径**: `/api`
- **认证方式**: 待实现（当前未配置认证）
- **返回格式**: JSON
- **状态码**: 标准 HTTP 状态码

## 1. 用户管理 API

### 1.1 获取用户列表

- **URL**: `/api/users`
- **方法**: `GET`
- **描述**: 获取所有用户的列表
- **参数**: 无
- **返回示例**:
  ```json
  {
    "code": 200,
    "data": [
      {
        "id": 1,
        "username": "admin",
        "email": "admin@example.com",
        "role": "ADMIN",
        "createdAt": "2023-01-01T00:00:00Z"
      },
      {
        "id": 2,
        "username": "user1",
        "email": "user1@example.com",
        "role": "USER",
        "createdAt": "2023-01-02T00:00:00Z"
      }
    ],
    "message": "success"
  }
  ```

### 1.2 获取用户详情

- **URL**: `/api/users/{id}`
- **方法**: `GET`
- **描述**: 根据用户ID获取用户详细信息
- **参数**:
  - `id` (路径参数): 用户ID
- **返回示例**:
  ```json
  {
    "code": 200,
    "data": {
      "id": 1,
      "username": "admin",
      "email": "admin@example.com",
      "role": "ADMIN",
      "createdAt": "2023-01-01T00:00:00Z",
      "updatedAt": "2023-01-01T00:00:00Z"
    },
    "message": "success"
  }
  ```

### 1.3 创建用户

- **URL**: `/api/users`
- **方法**: `POST`
- **描述**: 创建新用户
- **请求体**:
  ```json
  {
    "username": "newuser",
    "email": "newuser@example.com",
    "password": "password123",
    "role": "USER"
  }
  ```
- **返回示例**:
  ```json
  {
    "code": 200,
    "data": {
      "id": 3,
      "username": "newuser",
      "email": "newuser@example.com",
      "role": "USER",
      "createdAt": "2023-01-03T00:00:00Z"
    },
    "message": "success"
  }
  ```

### 1.4 更新用户信息

- **URL**: `/api/users/{id}`
- **方法**: `PUT`
- **描述**: 更新指定用户的信息
- **参数**:
  - `id` (路径参数): 用户ID
- **请求体**:
  ```json
  {
    "username": "updateduser",
    "email": "updated@example.com",
    "role": "USER"
  }
  ```
- **返回示例**:
  ```json
  {
    "code": 200,
    "data": {
      "id": 1,
      "username": "updateduser",
      "email": "updated@example.com",
      "role": "USER",
      "updatedAt": "2023-01-03T00:00:00Z"
    },
    "message": "success"
  }
  ```

### 1.5 删除用户

- **URL**: `/api/users/{id}`
- **方法**: `DELETE`
- **描述**: 删除指定用户
- **参数**:
  - `id` (路径参数): 用户ID
- **返回示例**:
  ```json
  {
    "code": 200,
    "data": null,
    "message": "success"
  }
  ```

## 2. 产品管理 API

### 2.1 获取所有产品

- **URL**: `/api/products`
- **方法**: `GET`
- **描述**: 获取所有产品列表，支持分页
- **参数**:
  - `page` (查询参数): 页码（可选，默认1）
  - `size` (查询参数): 每页数量（可选，默认10）
  - `keyword` (查询参数): 搜索关键词（可选）
  - `categoryId` (查询参数): 分类ID（可选）
- **返回示例**:
  ```json
  {
    "code": 200,
    "data": {
      "content": [
        {
          "id": 1,
          "name": "产品1",
          "description": "产品描述1",
          "price": 99.99,
          "stock": 100,
          "categoryId": 1,
          "imageUrl": "https://example.com/image1.jpg",
          "status": "ACTIVE"
        }
      ],
      "totalElements": 1,
      "totalPages": 1,
      "number": 0,
      "size": 10
    },
    "message": "success"
  }
  ```

### 2.2 获取活跃产品

- **URL**: `/api/products/active`
- **方法**: `GET`
- **描述**: 获取所有状态为活跃的产品
- **参数**: 无
- **返回示例**:
  ```json
  {
    "code": 200,
    "data": [
      {
        "id": 1,
        "name": "产品1",
        "price": 99.99,
        "imageUrl": "https://example.com/image1.jpg"
      }
    ],
    "message": "success"
  }
  ```

### 2.3 按分类获取产品

- **URL**: `/api/products/category/{categoryId}`
- **方法**: `GET`
- **描述**: 根据分类ID获取产品列表
- **参数**:
  - `categoryId` (路径参数): 分类ID
- **返回示例**:
  ```json
  {
    "code": 200,
    "data": [
      {
        "id": 1,
        "name": "产品1",
        "price": 99.99,
        "stock": 100
      }
    ],
    "message": "success"
  }
  ```

### 2.4 搜索产品

- **URL**: `/api/products/search`
- **方法**: `GET`
- **描述**: 根据关键词搜索产品
- **参数**:
  - `keyword` (查询参数): 搜索关键词
  - `page` (查询参数): 页码（可选，默认1）
  - `size` (查询参数): 每页数量（可选，默认10）
- **返回示例**:
  ```json
  {
    "code": 200,
    "data": {
      "content": [
        {
          "id": 1,
          "name": "产品1",
          "description": "匹配关键词的产品描述",
          "price": 99.99
        }
      ],
      "totalElements": 1,
      "totalPages": 1
    },
    "message": "success"
  }
  ```

### 2.5 获取产品详情

- **URL**: `/api/products/{id}`
- **方法**: `GET`
- **描述**: 根据产品ID获取产品详细信息
- **参数**:
  - `id` (路径参数): 产品ID
- **返回示例**:
  ```json
  {
    "code": 200,
    "data": {
      "id": 1,
      "name": "产品1",
      "description": "产品详细描述",
      "price": 99.99,
      "stock": 100,
      "categoryId": 1,
      "imageUrl": "https://example.com/image1.jpg",
      "status": "ACTIVE",
      "createdAt": "2023-01-01T00:00:00Z",
      "updatedAt": "2023-01-02T00:00:00Z"
    },
    "message": "success"
  }
  ```

### 2.6 创建产品

- **URL**: `/api/products`
- **方法**: `POST`
- **描述**: 创建新产品
- **请求体**:
  ```json
  {
    "name": "新产品",
    "description": "产品描述",
    "price": 199.99,
    "stock": 50,
    "categoryId": 1,
    "imageUrl": "https://example.com/newimage.jpg",
    "status": "ACTIVE"
  }
  ```
- **返回示例**:
  ```json
  {
    "code": 200,
    "data": {
      "id": 2,
      "name": "新产品",
      "description": "产品描述",
      "price": 199.99,
      "stock": 50,
      "categoryId": 1,
      "imageUrl": "https://example.com/newimage.jpg",
      "status": "ACTIVE"
    },
    "message": "success"
  }
  ```

### 2.7 更新产品

- **URL**: `/api/products/{id}`
- **方法**: `PUT`
- **描述**: 更新指定产品信息
- **参数**:
  - `id` (路径参数): 产品ID
- **请求体**:
  ```json
  {
    "name": "更新后的产品名",
    "description": "更新后的描述",
    "price": 149.99,
    "stock": 45,
    "status": "ACTIVE"
  }
  ```
- **返回示例**:
  ```json
  {
    "code": 200,
    "data": {
      "id": 1,
      "name": "更新后的产品名",
      "description": "更新后的描述",
      "price": 149.99,
      "stock": 45,
      "categoryId": 1,
      "imageUrl": "https://example.com/image1.jpg",
      "status": "ACTIVE",
      "updatedAt": "2023-01-03T00:00:00Z"
    },
    "message": "success"
  }
  ```

### 2.8 删除产品

- **URL**: `/api/products/{id}`
- **方法**: `DELETE`
- **描述**: 删除指定产品
- **参数**:
  - `id` (路径参数): 产品ID
- **返回示例**:
  ```json
  {
    "code": 200,
    "data": null,
    "message": "success"
  }
  ```

### 2.9 更新产品库存

- **URL**: `/api/products/{id}/stock`
- **方法**: `PUT`
- **描述**: 更新指定产品的库存数量
- **参数**:
  - `id` (路径参数): 产品ID
  - `quantity` (查询参数): 新的库存数量
- **返回示例**:
  ```json
  {
    "code": 200,
    "data": {
      "id": 1,
      "stock": 150
    },
    "message": "success"
  }
  ```

## 3. 订单管理 API

### 3.1 获取所有订单

- **URL**: `/api/orders`
- **方法**: `GET`
- **描述**: 获取所有订单列表
- **参数**:
  - `page` (查询参数): 页码（可选，默认1）
  - `size` (查询参数): 每页数量（可选，默认10）
- **返回示例**:
  ```json
  {
    "code": 200,
    "data": [
      {
        "id": 1,
        "orderNo": "ORD-20230101-0001",
        "userId": 1,
        "totalAmount": 199.98,
        "status": "COMPLETED",
        "createdAt": "2023-01-01T10:00:00Z"
      }
    ],
    "message": "success"
  }
  ```

### 3.2 获取用户订单

- **URL**: `/api/orders/user/{userId}`
- **方法**: `GET`
- **描述**: 根据用户ID获取订单列表
- **参数**:
  - `userId` (路径参数): 用户ID
- **返回示例**:
  ```json
  {
    "code": 200,
    "data": [
      {
        "id": 1,
        "orderNo": "ORD-20230101-0001",
        "totalAmount": 199.98,
        "status": "COMPLETED",
        "createdAt": "2023-01-01T10:00:00Z"
      }
    ],
    "message": "success"
  }
  ```

### 3.3 按状态获取订单

- **URL**: `/api/orders/status/{status}`
- **方法**: `GET`
- **描述**: 根据订单状态获取订单列表
- **参数**:
  - `status` (路径参数): 订单状态（如：PENDING, COMPLETED, CANCELLED）
- **返回示例**:
  ```json
  {
    "code": 200,
    "data": [
      {
        "id": 1,
        "orderNo": "ORD-20230101-0001",
        "userId": 1,
        "totalAmount": 199.98,
        "createdAt": "2023-01-01T10:00:00Z"
      }
    ],
    "message": "success"
  }
  ```

### 3.4 获取订单详情

- **URL**: `/api/orders/{id}`
- **方法**: `GET`
- **描述**: 根据订单ID获取订单详细信息，包括订单项
- **参数**:
  - `id` (路径参数): 订单ID
- **返回示例**:
  ```json
  {
    "code": 200,
    "data": {
      "order": {
        "id": 1,
        "orderNo": "ORD-20230101-0001",
        "userId": 1,
        "totalAmount": 199.98,
        "status": "COMPLETED",
        "createdAt": "2023-01-01T10:00:00Z",
        "updatedAt": "2023-01-01T10:05:00Z"
      },
      "items": [
        {
          "id": 1,
          "productId": 1,
          "productName": "产品1",
          "quantity": 2,
          "price": 99.99
        }
      ]
    },
    "message": "success"
  }
  ```

### 3.5 按订单号获取订单

- **URL**: `/api/orders/orderNo/{orderNo}`
- **方法**: `GET`
- **描述**: 根据订单号获取订单详细信息
- **参数**:
  - `orderNo` (路径参数): 订单号
- **返回示例**:
  ```json
  {
    "code": 200,
    "data": {
      "order": {
        "id": 1,
        "orderNo": "ORD-20230101-0001",
        "userId": 1,
        "totalAmount": 199.98,
        "status": "COMPLETED"
      },
      "items": [
        {
          "id": 1,
          "productId": 1,
          "productName": "产品1",
          "quantity": 2,
          "price": 99.99
        }
      ]
    },
    "message": "success"
  }
  ```

### 3.6 创建订单

- **URL**: `/api/orders`
- **方法**: `POST`
- **描述**: 创建新订单
- **请求体**:
  ```json
  {
    "userId": 1,
    "items": [
      {
        "productId": 1,
        "quantity": 2
      },
      {
        "productId": 2,
        "quantity": 1
      }
    ]
  }
  ```
- **返回示例**:
  ```json
  {
    "code": 200,
    "data": {
      "id": 2,
      "orderNo": "ORD-20230102-0001",
      "userId": 1,
      "totalAmount": 399.97,
      "status": "PENDING",
      "createdAt": "2023-01-02T10:00:00Z"
    },
    "message": "success"
  }
  ```

### 3.7 更新订单状态

- **URL**: `/api/orders/{id}/status`
- **方法**: `PUT`
- **描述**: 更新订单状态
- **参数**:
  - `id` (路径参数): 订单ID
  - `status` (查询参数): 新的订单状态
- **返回示例**:
  ```json
  {
    "code": 200,
    "data": {
      "id": 1,
      "orderNo": "ORD-20230101-0001",
      "status": "SHIPPING",
      "updatedAt": "2023-01-02T15:00:00Z"
    },
    "message": "success"
  }
  ```

### 3.8 取消订单

- **URL**: `/api/orders/{id}/cancel`
- **方法**: `PUT`
- **描述**: 取消指定订单
- **参数**:
  - `id` (路径参数): 订单ID
- **返回示例**:
  ```json
  {
    "code": 200,
    "data": null,
    "message": "success"
  }
  ```

### 3.9 获取订单项

- **URL**: `/api/orders/{id}/items`
- **方法**: `GET`
- **描述**: 获取指定订单的所有订单项
- **参数**:
  - `id` (路径参数): 订单ID
- **返回示例**:
  ```json
  {
    "code": 200,
    "data": [
      {
        "id": 1,
        "productId": 1,
        "productName": "产品1",
        "quantity": 2,
        "price": 99.99
      },
      {
        "id": 2,
        "productId": 2,
        "productName": "产品2",
        "quantity": 1,
        "price": 199.99
      }
    ],
    "message": "success"
  }
  ```

## 4. 错误响应格式

所有API错误响应遵循以下格式：

```json
{
  "code": 错误码,
  "message": "错误信息",
  "details": "详细错误描述（可选）"
}
```

## 5. 状态码说明

- **200**: 成功
- **400**: 请求参数错误
- **401**: 未授权
- **403**: 禁止访问
- **404**: 资源不存在
- **500**: 服务器内部错误

## 6. 注意事项

1. 所有API调用需要在请求头中添加适当的Content-Type（如application/json）
2. 部分API可能需要身份验证（待实现）
3. 数据库连接需要配置正确的数据库URL、用户名和密码
4. 生产环境部署时应修改默认的数据库配置和密码
# 用户评价系统 API 接口文档

## 基础信息

- 基础路径 : /api/reviews (注意：应用配置了context-path=/api)
- 认证方式 : JWT Token
- 响应格式 : JSON

## 统一响应格式

所有接口都使用统一的响应格式：

```
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

- code : 状态码，200表示成功
- message : 响应消息
- data : 具体数据内容

## 接口列表

### 1. 提交AI对话评价

接口地址 : POST /api/reviews/chat

功能描述 : 用户提交对AI对话功能的评价

请求参数 :

```
{
  "overallRating": 5,
  "qualityRating": 4,
  "speedRating": 5,
  "comment": "AI对话功能很好用，回答准
  确"
}
```

参数说明 :

参数名 类型 必填 说明 overallRating Integer 是 整体满意度评分(1-5) qualityRating Integer 是 质量评分(1-5) speedRating Integer 是 速度评分(1-5) comment String 否 评价意见

验证规则 :

- 所有评分字段必填且必须在1-5之间
  响应示例 :

```
{
  "code": 200,
  "message": "评价提交成功",
  "data": {
    "id": 1,
    "username": "张三",
    "featureType": "chat",
    "overallRating": 5,
    "qualityRating": 4,
    "speedRating": 5,
    "comment": "AI对话功能很好用，回答
    准确",
    "createdAt": 
    "2024-01-01T10:30:00"
  }
}
```

### 2. 提交智能生图评价

接口地址 : POST /api/reviews/image

功能描述 : 用户提交对智能生图功能的评价

请求参数 : 与AI对话评价相同

响应示例 : 与AI对话评价相同，featureType为"image"

### 3. 获取AI对话评价统计

接口地址 : GET /api/reviews/chat/stats

功能描述 : 获取AI对话功能的评价统计数据

请求参数 : 无

响应示例 :

```
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "averageOverallRating": 4.5,
    "averageQualityRating": 4.3,
    "averageSpeedRating": 4.7,
    "totalReviews": 150
  }
}
```

响应字段说明 :

字段名 类型 说明 averageOverallRating Double 平均整体满意度评分 averageQualityRating Double 平均质量评分 averageSpeedRating Double 平均速度评分 totalReviews Long 总评价数量

### 4. 获取智能生图评价统计

接口地址 : GET /api/reviews/image/stats

功能描述 : 获取智能生图功能的评价统计数据

请求参数 : 无

响应格式 : 与AI对话评价统计相同

### 5. 获取AI对话评价列表

接口地址 : GET /api/reviews/chat/list

功能描述 : 分页获取AI对话功能的评价列表

请求参数 :

参数名 类型 必填 默认值 说明 page Integer 否 1 页码 pageSize Integer 否 3 每页数量

响应示例 :

```
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "reviews": [
      {
        "id": 1,
        "username": "张三",
        "featureType": "chat",
        "overallRating": 5,
        "qualityRating": 4,
        "speedRating": 5,
        "comment": "AI对话功能很好用，
        回答准确",
        "createdAt": 
        "2024-01-01T10:30:00"
      }
    ],
    "page": 1,
    "totalPages": 10
  }
}
```

### 6. 获取智能生图评价列表

接口地址 : GET /api/reviews/image/list

功能描述 : 分页获取智能生图功能的评价列表

请求参数 : 与AI对话评价列表相同

响应格式 : 与AI对话评价列表相同

## 错误码说明

错误码 说明 200 操作成功 400 请求参数错误 401 未授权，需要登录 403 权限不足 500 服务器内部错误

## 注意事项

1. 1. 所有接口都需要JWT认证，在请求头中携带Authorization字段
2. 2. 提交评价接口会自动获取当前登录用户信息
3. 3. 评分字段必须在1-5之间，否则会返回验证错误
4. 4. 分页查询默认每页返回3条记录

## 调用示例

### 提交AI对话评价

```
curl -X POST http://localhost:8080/
api/reviews/chat \
  -H "Content-Type: application/
  json" \
  -H "Authorization: Bearer 
  your-jwt-token" \
  -d '{
    "overallRating": 5,
    "qualityRating": 4,
    "speedRating": 5,
    "comment": "AI对话功能很好用"
  }'
```

### 获取AI对话评价统计

```
curl -X GET http://localhost:8080/
api/reviews/chat/stats \
  -H "Authorization: Bearer 
  your-jwt-toke
```

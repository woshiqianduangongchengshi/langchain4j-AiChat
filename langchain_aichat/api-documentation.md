# AI智能助手 - 后端接口文档

## 1. 个人信息相关接口

### 1.1 修改用户密码

接口描述： 修改当前登录用户的密码，用于个人主页密码修改功能

请求方式： PUT

接口地址： /api/auth/password

请求头：

```
{
  "Authorization": "Bearer {token}"
}
```

请求参数：

```
{
  "currentPassword": "当前密码",
  "newPassword": "新密码"
}
```

参数说明：

- currentPassword : 当前密码，必填
- newPassword : 新密码，必填
  响应示例：

```
{
  "code": 200,
  "message": "密码修改成功"
}
```

### 1.2 更新用户信息

接口描述： 更新当前登录用户的个人信息

请求方式： PUT

接口地址： /api/auth/user

请求头：

```
{
  "Authorization": "Bearer {token}"
}
```

请求参数：

```
{
  "username": "新用户名",
  "email": "新邮箱",
  "birthday": "2000-01-01"
}
```

响应示例：

```
{
  "code": 200,
  "message": "用户信息更新成功",
  "data": {
    "id": 1,
    "username": "新用户名",
    "email": "新邮箱",
    "birthday": "2000-01-01",
    "createdAt": 
    "2024-01-01T00:00:00Z",
    "updatedAt": 
    "2024-01-02T00:00:00Z"
  }
}
```

## 2. 功能评价相关接口

### 2.1 提交AI对话功能评价

接口描述： 提交用户对AI对话功能的评价

请求方式： POST

接口地址： /api/reviews/chat

请求头：

```
{
  "Authorization": "Bearer {token}"
}
```

请求参数：

```
{
  "overallRating": 5,
  "qualityRating": 4,
  "speedRating": 5,
  "comment": "AI对话功能非常好，回答质
  量高，响应速度快"
}
```

参数说明：

- overallRating : 整体满意度，1-5分，5分为最高，必填
- qualityRating : 回答质量，1-5分，5分为最高，必填
- speedRating : 响应速度，1-5分，5分为最高，必填
- comment : 评价意见，可选
  响应示例：

```
{
  "code": 200,
  "message": "评价提交成功",
  "data": {
    "id": 1,
    "userId": 1,
    "featureType": "chat",
    "overallRating": 5,
    "qualityRating": 4,
    "speedRating": 5,
    "comment": "AI对话功能非常好，回答
    质量高，响应速度快",
    "createdAt": 
    "2024-05-20T10:30:00Z"
  }
}
```

### 2.2 提交智能生图功能评价

接口描述： 提交用户对智能生图功能的评价

请求方式： POST

接口地址： /api/reviews/image

请求头：

```
{
  "Authorization": "Bearer {token}"
}
```

请求参数：

```
{
  "overallRating": 4,
  "qualityRating": 5,
  "speedRating": 3,
  "comment": "生成的图片质量很高，但生成
  速度有待提升"
}
```

参数说明：

- overallRating : 整体满意度，1-5分，5分为最高，必填
- qualityRating : 图片质量，1-5分，5分为最高，必填
- speedRating : 生成速度，1-5分，5分为最高，必填
- comment : 评价意见，可选
  响应示例：

```
{
  "code": 200,
  "message": "评价提交成功",
  "data": {
    "id": 2,
    "userId": 1,
    "featureType": "image",
    "overallRating": 4,
    "qualityRating": 5,
    "speedRating": 3,
    "comment": "生成的图片质量很高，但
    生成速度有待提升",
    "createdAt": 
    "2024-05-20T10:35:00Z"
  }
}
```

### 2.3 获取AI对话评价统计数据

接口描述： 获取AI对话功能的评价统计信息，用于评价页面的统计展示

请求方式： GET

接口地址： /api/reviews/chat/stats

请求头：

```
{
  "Authorization": "Bearer {token}"
}
```

响应示例：

```
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "averageOverallRating": 4.5,
    "averageQualityRating": 4.3,
    "averageSpeedRating": 4.6,
    "totalReviews": 100
  }
}
```

参数说明：

- averageOverallRating : 整体满意度平均分
- averageQualityRating : 回答质量平均分
- averageSpeedRating : 响应速度平均分
- totalReviews : 总评价数量

### 2.4 获取智能生图评价统计数据

接口描述： 获取智能生图功能的评价统计信息，用于评价页面的统计展示

请求方式： GET

接口地址： /api/reviews/image/stats

请求头：

```
{
  "Authorization": "Bearer {token}"
}
```

响应示例：

```
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "averageOverallRating": 4.2,
    "averageQualityRating": 4.5,
    "averageSpeedRating": 3.8,
    "totalReviews": 80
  }
}
```

参数说明：

- averageOverallRating : 整体满意度平均分
- averageQualityRating : 图片质量平均分
- averageSpeedRating : 生成速度平均分
- totalReviews : 总评价数量

### 2.5 获取AI对话评价列表

接口描述： 获取AI对话功能的评价列表，支持分页，用于评价页面的评价列表展示

请求方式： GET

接口地址： /api/reviews/chat/list

请求头：

```
{
  "Authorization": "Bearer {token}"
}
```

查询参数：

- page : 页码，默认为1
- pageSize : 每页数量，默认为3（前端设置为3）
  响应示例：

```
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "reviews": [
      {
        "id": 1,
        "username": "user1",
        "overallRating": 5,
        "qualityRating": 4,
        "speedRating": 5,
        "comment": "AI对话功能非常好，
        回答质量高，响应速度快",
        "createdAt": 
        "2024-05-20T10:30:00Z"
      },
      {
        "id": 2,
        "username": "user2",
        "overallRating": 4,
        "qualityRating": 5,
        "speedRating": 3,
        "comment": "回答质量不错，但响
        应速度有待提升",
        "createdAt": 
        "2024-05-19T15:20:00Z"
      }
    ],
    "page": 1,
    "totalPages": 10
  }
}
```

参数说明：

- reviews : 评价列表
- page : 当前页码
- totalPages : 总页数
  注意： 前端会处理createdAt字段，如果包含时间部分，会只显示日期部分

### 2.6 获取智能生图评价列表

接口描述： 获取智能生图功能的评价列表，支持分页，用于评价页面的评价列表展示

请求方式： GET

接口地址： /api/reviews/image/list

请求头：

```
{
  "Authorization": "Bearer {token}"
}
```

查询参数：

- page : 页码，默认为1
- pageSize : 每页数量，默认为3（前端设置为3）
  响应示例：

```
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "reviews": [
      {
        "id": 3,
        "username": "user3",
        "overallRating": 4,
        "qualityRating": 5,
        "speedRating": 3,
        "comment": "生成的图片质量很
        高，但生成速度有待提升",
        "createdAt": 
        "2024-05-20T10:35:00Z"
      },
      {
        "id": 4,
        "username": "user4",
        "overallRating": 5,
        "qualityRating": 5,
        "speedRating": 4,
        "comment": "图片生成效果很好，
        速度也很快",
        "createdAt": 
        "2024-05-18T14:10:00Z"
      }
    ],
    "page": 1,
    "totalPages": 8
  }
}
```

参数说明：

- reviews : 评价列表
- page : 当前页码
- totalPages : 总页数
  注意： 前端会处理createdAt字段，如果包含时间部分，会只显示日期部分

## 3. 错误码说明

错误码 说明 200 请求成功 400 请求参数错误 401 未授权，token无效或过期 403 权限不足 404 资源不存在 500 服务器内部错误

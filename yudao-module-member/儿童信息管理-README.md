# 儿童信息管理模块

## 功能概述

这是为自闭症互助平台开发的儿童信息管理模块，允许家长/监护人管理与自己关联的孩子信息。

## 数据库表

### member_children - 儿童个人信息表

| 字段名 | 类型 | 说明 | 示例值 |
|--------|------|------|--------|
| id | bigint | 自增主键 | 1 |
| user_id | bigint | 关联用户ID（监护人） | 247 |
| nickname | varchar(50) | 孩子昵称 | "小明" |
| avatar_url | varchar(512) | 孩子头像地址 | "http://..." |
| gender | varchar(10) | 性别 | male/female/unknown |
| age_years | tinyint | 实际年龄（岁） | 5 |
| age_group | varchar(20) | 年龄组 | "3-6" |
| diagnosis_date | datetime | 诊断日期 | 2023-01-15 10:30:00 |
| guardian_relation | varchar(20) | 监护人关系 | father/mother/grandfather/grandmother/other |
| status_tags | varchar(2000) | 状态标签（JSON数组） | ["LANGUAGE_DELAY", "SOCIAL_DIFFICULTY"] |
| goal_tags | varchar(2000) | 目标标签（JSON数组） | ["LANGUAGE_MIMIC", "KINDERGARTEN_PREP"] |
| description | varchar(500) | 备注说明 | "注意事项..." |
| creator | varchar(64) | 创建者 | |
| create_time | datetime | 创建时间 | |
| updater | varchar(64) | 更新者 | |
| update_time | datetime | 更新时间 | |
| deleted | bit(1) | 是否删除 | 0/1 |
| tenant_id | bigint | 租户编号 | 0 |

#### 状态标签 (statusTags)

常见的状态标签示例：
- `LANGUAGE_DELAY` - 语言发育迟缓
- `SOCIAL_DIFFICULTY` - 社交困难
- `SENSORY_SENSITIVITY` - 感觉敏感
- `REPETITIVE_BEHAVIOR` - 重复性行为
- `ATTENTION_DEFICIT` - 注意力缺陷
- `HYPERACTIVITY` - 多动
- `EMOTIONAL_REGULATION` - 情绪调节困难

#### 目标标签 (goalTags)

常见的目标标签示例：
- `LANGUAGE_MIMIC` - 语言模仿
- `KINDERGARTEN_PREP` - 幼儿园准备
- `SOCIAL_SKILLS` - 社交技能
- `DAILY_LIVING` - 日常生活技能
- `COMMUNICATION` - 沟通能力
- `EMOTION_CONTROL` - 情绪控制
- `INDEPENDENCE` - 独立性培养

## API 接口

### 基础路径
```
/member/children
```

### 接口列表

#### 1. 创建儿童信息
- **URL**: `POST /member/children/create`
- **说明**: 创建一条新的儿童信息记录
- **请求体**:
```json
{
  "nickname": "小明",
  "avatarUrl": "http://example.com/avatar.jpg",
  "gender": "male",
  "ageYears": 5,
  "ageGroup": "3-6",
  "diagnosisDate": "2023-01-15 10:30:00",
  "guardianRelation": "father",
  "statusTags": ["LANGUAGE_DELAY", "SOCIAL_DIFFICULTY"],
  "goalTags": ["LANGUAGE_MIMIC", "KINDERGARTEN_PREP"],
  "description": "喜欢听音乐，对色彩敏感"
}
```
- **响应**: 
```json
{
  "code": 0,
  "data": 1,  // 返回创建的儿童信息ID
  "msg": "成功"
}
```

#### 2. 更新儿童信息
- **URL**: `PUT /member/children/update`
- **说明**: 更新已有的儿童信息
- **请求体**:
```json
{
  "id": 1,
  "nickname": "小明",
  "avatarUrl": "http://example.com/avatar2.jpg",
  "gender": "male",
  "ageYears": 6,
  "ageGroup": "6-12",
  "diagnosisDate": "2023-01-15 10:30:00",
  "guardianRelation": "father",
  "statusTags": ["LANGUAGE_DELAY", "SOCIAL_DIFFICULTY", "SENSORY_SENSITIVITY"],
  "goalTags": ["LANGUAGE_MIMIC", "KINDERGARTEN_PREP", "SOCIAL_SKILLS"],
  "description": "喜欢听音乐，对色彩敏感，最近进步很大"
}
```
- **响应**: 
```json
{
  "code": 0,
  "data": true,
  "msg": "成功"
}
```

#### 3. 删除儿童信息
- **URL**: `DELETE /member/children/delete?id={id}`
- **说明**: 删除指定的儿童信息（软删除）
- **参数**: 
  - `id` (required): 儿童信息ID
- **响应**: 
```json
{
  "code": 0,
  "data": true,
  "msg": "成功"
}
```

#### 4. 获取儿童信息详情
- **URL**: `GET /member/children/get?id={id}`
- **说明**: 获取单条儿童信息详情
- **参数**: 
  - `id` (required): 儿童信息ID
- **响应**: 
```json
{
  "code": 0,
  "data": {
    "id": 1,
    "nickname": "小明",
    "avatarUrl": "http://example.com/avatar.jpg",
    "gender": "male",
    "ageYears": 5,
    "ageGroup": "3-6",
    "diagnosisDate": "2023-01-15 10:30:00",
    "guardianRelation": "father",
    "statusTags": ["LANGUAGE_DELAY", "SOCIAL_DIFFICULTY"],
    "goalTags": ["LANGUAGE_MIMIC", "KINDERGARTEN_PREP"],
    "description": "喜欢听音乐，对色彩敏感",
    "createTime": "2024-01-15 14:30:00"
  },
  "msg": "成功"
}
```

#### 5. 获取儿童信息列表
- **URL**: `GET /member/children/list`
- **说明**: 获取当前登录用户的所有儿童信息列表
- **响应**: 
```json
{
  "code": 0,
  "data": [
    {
      "id": 1,
      "nickname": "小明",
      "avatarUrl": "http://example.com/avatar.jpg",
      "gender": "male",
      "ageYears": 5,
      "ageGroup": "3-6",
      "diagnosisDate": "2023-01-15 10:30:00",
      "guardianRelation": "father",
      "statusTags": ["LANGUAGE_DELAY", "SOCIAL_DIFFICULTY"],
      "goalTags": ["LANGUAGE_MIMIC", "KINDERGARTEN_PREP"],
      "description": "喜欢听音乐，对色彩敏感",
      "createTime": "2024-01-15 14:30:00"
    }
  ],
  "msg": "成功"
}
```

## 代码结构

```
yudao-module-member/
├── src/main/java/cn/iocoder/yudao/module/member/
│   ├── controller/app/children/
│   │   ├── AppChildrenController.java        # 控制器
│   │   ├── AppChildrenController.http        # HTTP 测试文件
│   │   └── vo/
│   │       ├── AppChildrenBaseVO.java        # 基础 VO
│   │       ├── AppChildrenCreateReqVO.java   # 创建请求 VO
│   │       ├── AppChildrenUpdateReqVO.java   # 更新请求 VO
│   │       └── AppChildrenRespVO.java        # 响应 VO
│   ├── convert/children/
│   │   └── ChildrenConvert.java              # 对象转换器
│   ├── dal/
│   │   ├── dataobject/children/
│   │   │   └── MemberChildrenDO.java         # 数据实体
│   │   └── mysql/children/
│   │       └── MemberChildrenMapper.java     # MyBatis Mapper
│   └── service/children/
│       ├── ChildrenService.java              # Service 接口
│       └── ChildrenServiceImpl.java          # Service 实现
└── sql/
    ├── mysql/member_children.sql             # MySQL 表结构
    └── postgresql/member_children.sql        # PostgreSQL 表结构
```

## 使用说明

### 1. 数据库初始化

根据你使用的数据库类型，执行相应的 SQL 脚本：

**MySQL**:
```bash
mysql -u root -p your_database < sql/mysql/member_children.sql
```

**PostgreSQL**:
```bash
psql -U postgres -d your_database -f sql/postgresql/member_children.sql
```

### 2. 测试接口

使用 IntelliJ IDEA 或其他支持 HTTP 文件的工具，打开：
```
yudao-module-member/src/main/java/cn/iocoder/yudao/module/member/controller/app/children/AppChildrenController.http
```

在测试前，需要：
1. 先登录获取 token
2. 配置好 baseUrl 和 tenantId
3. 执行各个测试用例

### 3. 权限控制

所有接口都需要登录认证，通过 `getLoginUserId()` 获取当前登录用户ID。系统会自动校验：
- 创建：只能为当前登录用户创建儿童信息
- 更新/删除/查询：只能操作属于当前登录用户的儿童信息

### 4. 数据验证

- **nickname**: 必填，孩子昵称
- **gender**: 必填，只能是 `male`、`female` 或 `unknown`
- **guardianRelation**: 可选，只能是 `father`、`mother`、`grandfather`、`grandmother` 或 `other`
- **statusTags**: 可选，JSON 数组格式
- **goalTags**: 可选，JSON 数组格式

## 错误码

| 错误码 | 说明 |
|--------|------|
| 1_004_013_000 | 儿童信息不存在 |

## 注意事项

1. **数据隔离**: 每个用户只能访问和管理自己关联的儿童信息
2. **软删除**: 删除操作为软删除，数据不会真正从数据库中删除
3. **JSON 字段**: statusTags 和 goalTags 在数据库中存储为 JSON 字符串，在应用层自动转换为 List
4. **多租户**: 支持多租户架构，通过 tenant_id 字段隔离数据
5. **审计字段**: 自动记录 creator、create_time、updater、update_time

## 未来扩展

可以考虑添加以下功能：
- 儿童成长记录功能
- 儿童照片墙功能
- 家长分享儿童进步的功能
- 儿童康复计划管理
- 儿童医疗记录管理
- 数据统计和分析


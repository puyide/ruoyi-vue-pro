# 同行小组功能模块

## 📌 功能概述

同行小组是为自闭症儿童家长提供的互助交流平台，采用三层架构设计：

| 层级 | 职责 | 技术实现 |
|------|------|----------|
| **小程序** | 建组、关系、进度、任务 | 微信小程序 + ruoyi-vue-pro API |
| **NodeBB** | 沉淀、结构化讨论、知识库 | NodeBB 论坛（同步投影） |
| **微信群** | 即时情绪出口与临时沟通 | 群二维码 / 企业微信 |

### 关键设计原则

1. ✅ **业务库是唯一真相源** - 成员与权限数据存储在业务数据库，NodeBB 只是同步的"投影"
2. ✅ **Outbox/重试机制** - 异步同步到 NodeBB，避免直接在用户请求里硬同步

---

## 📁 项目结构

```
yudao-module-member/
├── sql/
│   └── peer_group.sql                    # 数据库表结构
│
├── src/main/java/.../member/
│   ├── dal/
│   │   ├── dataobject/peergroup/
│   │   │   ├── PeerGroupDO.java          # 同行小组实体
│   │   │   ├── PeerGroupMemberDO.java    # 小组成员实体
│   │   │   ├── PeerGroupJoinRequestDO.java # 加入申请实体
│   │   │   └── NodebbSyncOutboxDO.java   # 同步发件箱实体
│   │   └── mysql/peergroup/
│   │       ├── PeerGroupMapper.java
│   │       ├── PeerGroupMemberMapper.java
│   │       ├── PeerGroupJoinRequestMapper.java
│   │       └── NodebbSyncOutboxMapper.java
│   │
│   ├── enums/peergroup/
│   │   ├── PeerGroupMemberRoleEnum.java   # 成员角色枚举
│   │   ├── PeerGroupMemberStatusEnum.java # 成员状态枚举
│   │   ├── PeerGroupJoinModeEnum.java     # 加入模式枚举
│   │   ├── NodebbSyncTypeEnum.java        # 同步类型枚举
│   │   └── NodebbSyncStatusEnum.java      # 同步状态枚举
│   │
│   ├── service/peergroup/
│   │   ├── PeerGroupService.java          # 小组服务接口
│   │   ├── PeerGroupServiceImpl.java      # 小组服务实现
│   │   ├── NodebbGroupSyncService.java    # NodeBB同步接口
│   │   └── NodebbGroupSyncServiceImpl.java# NodeBB同步实现
│   │
│   ├── controller/
│   │   ├── admin/peergroup/
│   │   │   ├── PeerGroupController.java   # 管理后台API
│   │   │   └── vo/                        # 管理后台VO
│   │   └── app/peergroup/
│   │       ├── AppPeerGroupController.java# 小程序API
│   │       └── vo/                        # 小程序VO
│   │
│   ├── convert/peergroup/
│   │   └── PeerGroupConvert.java          # 对象转换器
│   │
│   └── job/
│       └── NodebbSyncJob.java             # 定时同步任务

miniprogram-autism-support/
├── pages/
│   ├── group/
│   │   ├── group.js                       # 小组列表页
│   │   ├── group.wxml
│   │   └── group.wxss
│   └── group-detail/
│       ├── group-detail.js                # 小组详情页
│       ├── group-detail.wxml
│       └── group-detail.wxss
└── utils/
    └── api.js                             # API封装（已添加小组API）
```

---

## 🗄️ 数据库表

### 1. peer_group - 同行小组表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 小组编号 |
| name | varchar(100) | 小组名称 |
| icon | varchar(50) | 图标（emoji） |
| theme | varchar(20) | 主题色 |
| category | varchar(50) | 分类 |
| tags | varchar(500) | 标签JSON |
| age_group | varchar(20) | 年龄组 |
| max_members | int | 最大成员数 |
| member_count | int | 当前成员数 |
| wechat_qr_url | varchar(512) | 微信群二维码 |
| wecom_url | varchar(512) | 企微群链接 |
| nodebb_group_id | bigint | NodeBB Group ID |
| nodebb_category_id | bigint | NodeBB 分类 ID |
| nodebb_sync_status | tinyint | 同步状态 |
| status | tinyint | 状态 |
| join_mode | tinyint | 加入模式 |

### 2. peer_group_member - 小组成员表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 成员记录编号 |
| group_id | bigint | 小组ID |
| user_id | bigint | 用户ID |
| role | varchar(20) | 角色：owner/admin/member |
| status | tinyint | 状态 |
| join_source | varchar(50) | 加入来源 |
| joined_at | datetime | 加入时间 |
| nodebb_synced | bit | NodeBB同步状态 |

### 3. nodebb_sync_outbox - 同步发件箱表

实现 Outbox 模式，确保异步可靠同步：

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 消息编号 |
| sync_type | varchar(50) | 同步类型 |
| operation | varchar(20) | 操作：CREATE/UPDATE/DELETE |
| biz_type | varchar(50) | 业务类型 |
| biz_id | bigint | 业务ID |
| payload | text | 同步数据JSON |
| status | tinyint | 状态 |
| retry_count | int | 重试次数 |
| next_retry_at | datetime | 下次重试时间 |

---

## 🔌 API 接口

### 小程序端 API

| 接口 | 方法 | 说明 |
|------|------|------|
| `/member/peer-group/my-groups` | GET | 获取我加入的小组 |
| `/member/peer-group/recommend` | GET | 获取推荐小组 |
| `/member/peer-group/list` | GET | 获取小组列表（按分类） |
| `/member/peer-group/detail` | GET | 获取小组详情 |
| `/member/peer-group/join` | POST | 加入小组 |
| `/member/peer-group/leave` | POST | 退出小组 |

### 管理后台 API

| 接口 | 方法 | 说明 |
|------|------|------|
| `/member/peer-group/create` | POST | 创建小组 |
| `/member/peer-group/update` | PUT | 更新小组 |
| `/member/peer-group/delete` | DELETE | 删除小组 |
| `/member/peer-group/page` | GET | 分页查询 |
| `/member/peer-group/member/add` | POST | 添加成员 |
| `/member/peer-group/member/remove` | DELETE | 移除成员 |
| `/member/peer-group/join-request/review` | PUT | 审核申请 |

---

## 🔄 NodeBB 同步机制

### 同步流程

```
业务操作（创建小组/加入成员等）
    ↓
写入 Outbox 表（同步数据JSON）
    ↓
定时 Job 扫描 Outbox（每分钟）
    ↓
调用 NodeBB API 执行同步
    ↓
成功 → 更新状态为成功
失败 → 指数退避重试（1/2/4/8/16分钟）
```

### 同步类型

- `GROUP_CREATE` - 创建小组 → 创建 NodeBB Group + 私密分类
- `GROUP_UPDATE` - 更新小组 → 更新 NodeBB Group
- `GROUP_DELETE` - 删除小组 → 删除 NodeBB Group
- `MEMBER_JOIN` - 成员加入 → 添加到 NodeBB Group
- `MEMBER_LEAVE` - 成员离开 → 从 NodeBB Group 移除
- `MEMBER_BAN` - 成员封禁 → 从 NodeBB Group 移除并封禁

---

## 🚀 部署步骤

### 1. 执行数据库脚本

```bash
# MySQL
mysql -u root -p your_database < sql/peer_group.sql

# PostgreSQL  
psql -U postgres -d your_database -f sql/peer_group.sql
```

### 2. 启动后端服务

确保 `application.yaml` 中配置了 NodeBB：

```yaml
yudao:
  member:
    nodebb:
      enabled: true
      url: http://localhost:4567
      master-token: 【你的Master_Token】
```

### 3. 更新小程序

小程序代码已更新，重新编译部署即可。

---

## 📱 小程序功能

### 小组列表页

- 显示我加入的小组（带活跃状态）
- 推荐小组（根据孩子档案智能推荐）
- 全部小组（按分类筛选）
- 一键加入/退出

### 小组详情页

- 小组信息展示
- 微信交流群入口（二维码弹窗）
- 今日话题讨论
- 消息列表（点赞、回复）
- 图片分享

---

## 🔮 后续扩展

1. **NodeBB 深度集成**
   - 实现真实的 NodeBB API 调用
   - SSO 跳转到论坛

2. **智能推荐**
   - 根据孩子年龄、状态标签智能匹配小组

3. **小组任务**
   - 关联训练任务、打卡功能

4. **数据统计**
   - 小组活跃度分析
   - 成员参与度报表

---

## 📝 注意事项

1. **权限控制**
   - 群主可以管理成员、审核申请
   - 普通成员只能查看和发言

2. **二维码管理**
   - 微信群二维码有7天有效期
   - 建议运营定期更新

3. **NodeBB 同步**
   - 同步失败会自动重试
   - 可在管理后台查看同步状态

---

## 📞 联系方式

如有问题，请联系开发团队。

